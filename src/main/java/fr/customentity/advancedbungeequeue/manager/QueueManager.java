package fr.customentity.advancedbungeequeue.manager;

import fr.customentity.advancedbungeequeue.AdvancedBungeeQueue;
import fr.customentity.advancedbungeequeue.data.QueuedPlayer;
import fr.customentity.advancedbungeequeue.runnable.ConnectRunnable;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;

import java.util.Queue;
import java.util.concurrent.*;

public class QueueManager {

    private AdvancedBungeeQueue plugin;

    private ConcurrentHashMap<ServerInfo, Queue<QueuedPlayer>> queue;
    private ScheduledExecutorService scheduledExecutorService;
    private boolean queueRunning = true;

    public QueueManager(AdvancedBungeeQueue plugin) {
        this.plugin = plugin;
        this.queue = new ConcurrentHashMap<>();
        this.scheduledExecutorService = new ScheduledThreadPoolExecutor(plugin.getConfigFile().getInt("thread-pool-size"));

        this.loadServersQueue();
        for(ServerInfo serverInfo : queue.keySet()) {
            this.scheduledExecutorService.scheduleAtFixedRate(new ConnectRunnable(plugin, serverInfo), this.plugin.getConfigFile().getLong("queue-speed"), this.plugin.getConfigFile().getLong("queue-speed"), TimeUnit.MILLISECONDS);
        }
    }

    public boolean isQueueRunning() {
        return queueRunning;
    }

    public void addPlayerInQueue(ProxiedPlayer proxiedPlayer, ServerInfo serverInfo) {
        if(plugin.getConfigFile().getStringList("default-servers").stream().noneMatch(s -> proxiedPlayer.getServer().getInfo().getName().contains(s))) {
            return;
        }
        if(proxiedPlayer.hasPermission(plugin.getConfigFile().getString("permissions.bypass"))) {
            proxiedPlayer.connect(serverInfo);
            return;
        }
        Queue<QueuedPlayer> queuedPlayers = queue.get(serverInfo);
        QueuedPlayer queuedPlayer = QueuedPlayer.get(proxiedPlayer);
        if(queuedPlayer.isWaiting())return;
        queuedPlayer.setTargetServer(serverInfo);
        queuedPlayers.add(queuedPlayer);
        queue.put(serverInfo, queuedPlayers);
    }

    public void setQueueRunning(boolean queueRunning) {
        this.queueRunning = queueRunning;
    }

    public void connectNextPlayers(ServerInfo serverInfo) {
        for(int i = 0; i < plugin.getConfigFile().getInt("player-amount"); i++) {
            Queue<QueuedPlayer> queuedPlayers = queue.get(serverInfo);
            QueuedPlayer queuedPlayer = queuedPlayers.peek();
            if(queuedPlayer == null)continue;
            queuedPlayer.getProxiedPlayer().connect(serverInfo, (result, error) -> {
                if(result) {
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
                queue.putIfAbsent(serverInfo, new ConcurrentLinkedQueue<>());
                if (!this.plugin.getConfigFile().getBoolean("use-same-queue")) break;
            }
        }
    }

    public ConcurrentHashMap<ServerInfo, Queue<QueuedPlayer>> getQueues() {
        return queue;
    }
}
