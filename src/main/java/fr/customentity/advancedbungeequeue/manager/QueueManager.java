package fr.customentity.advancedbungeequeue.manager;

import fr.customentity.advancedbungeequeue.AdvancedBungeeQueue;
import fr.customentity.advancedbungeequeue.data.QueuedPlayer;
import fr.customentity.advancedbungeequeue.runnable.ConnectRunnable;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;

public class QueueManager {

    private AdvancedBungeeQueue plugin;

    private ConcurrentHashMap<ServerInfo, List<QueuedPlayer>> queue;
    private ScheduledExecutorService scheduledExecutorService;
    private boolean queueRunning = true;

    public QueueManager(AdvancedBungeeQueue plugin) {
        this.plugin = plugin;
        this.queue = new ConcurrentHashMap<>();
        this.scheduledExecutorService = new ScheduledThreadPoolExecutor(plugin.getConfigFile().getInt("thread-pool-size"));

        this.loadServersQueue();
        for (ServerInfo serverInfo : queue.keySet()) {
            this.scheduledExecutorService.scheduleAtFixedRate(new ConnectRunnable(plugin, serverInfo), this.plugin.getConfigFile().getLong("queue-speed"), this.plugin.getConfigFile().getLong("queue-speed"), TimeUnit.MILLISECONDS);
        }
    }

    public boolean isQueueRunning() {
        return queueRunning;
    }

    public void addPlayerInQueue(ProxiedPlayer proxiedPlayer, ServerInfo serverInfo) {
        if (plugin.getConfigFile().getStringList("default-servers").stream().noneMatch(s -> proxiedPlayer.getServer().getInfo().getName().contains(s))) {
            return;
        }
        if (proxiedPlayer.hasPermission(plugin.getConfigFile().getString("permissions.bypass"))) {
            proxiedPlayer.connect(serverInfo);
            return;
        }
        List<QueuedPlayer> queuedPlayers = queue.get(serverInfo);
        if (this.plugin.getConfigFile().getBoolean("use-same-queue")) queuedPlayers = queue.values().stream().findFirst().get();
        if(queuedPlayers == null)return;
        
        QueuedPlayer queuedPlayer = new QueuedPlayer(proxiedPlayer, serverInfo, getPriority(proxiedPlayer));
        if (queuedPlayer.isWaiting()) return;
        queuedPlayer.setTargetServer(serverInfo);

        for(QueuedPlayer player : queuedPlayers) {
            if(player.getPriority() <= queuedPlayer.getPriority()) {
                queuedPlayers.add(queuedPlayers.indexOf(player), queuedPlayer);
            }
        }
    }

    public int getPriority(ProxiedPlayer proxiedPlayer) {
        for(String str : plugin.getConfigFile().getSection("priorities").getKeys()) {
            if(proxiedPlayer.hasPermission("advancedbungeequeue.priority." + str)) return plugin.getConfigFile().getInt("priorities." + str);
        }
        return plugin.getConfigFile().getInt("priorities.default", 0);
    }

    public int getPlayerPosition(ProxiedPlayer proxiedPlayer) {
        QueuedPlayer queuedPlayer = QueuedPlayer.get(proxiedPlayer);
        if (queue.get(queuedPlayer.getTargetServer()) != null)
            return queue.get(queuedPlayer.getTargetServer()).indexOf(queuedPlayer) + 1;
        return -1;
    }

    public void setQueueRunning(boolean queueRunning) {
        this.queueRunning = queueRunning;
    }

    public void connectNextPlayers(ServerInfo serverInfo) {
        for (int i = 0; i < plugin.getConfigFile().getInt("player-amount"); i++) {
            List<QueuedPlayer> queuedPlayers = queue.get(serverInfo);
            QueuedPlayer queuedPlayer = queuedPlayers.get(0);
            if (queuedPlayer == null) continue;
            queuedPlayer.getProxiedPlayer().connect(serverInfo, (result, error) -> {
                if (result) {
                    queuedPlayers.remove(queuedPlayer);
                } else {

                }
            }, ServerConnectEvent.Reason.PLUGIN);
        }
    }

    public void loadServersQueue() {
        for (String servers : this.plugin.getConfigFile().getStringList("queued-servers")) {
            ServerInfo serverInfo = this.plugin.getProxy().getServerInfo(servers);
            if (serverInfo != null) {
                queue.putIfAbsent(serverInfo, Collections.synchronizedList(new ArrayList<>()));
                if (!this.plugin.getConfigFile().getBoolean("use-same-queue")) break;
            }
        }
    }

    public ConcurrentHashMap<ServerInfo, List<QueuedPlayer>> getQueues() {
        return queue;
    }
}
