package fr.customentity.advancedbungeequeue.bungee.data;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.*;

public class QueuedPlayer {

    private static Set<QueuedPlayer> queuedPlayerSet = Collections.synchronizedSet(new HashSet<>());

    private UUID uuid;
    private ServerInfo targetServer;
    private Priority priority;
    private boolean connecting = false;

    public QueuedPlayer(ProxiedPlayer proxiedPlayer, ServerInfo targetServer, Priority priority) {
        this.uuid = proxiedPlayer.getUniqueId();
        this.targetServer = targetServer;
        this.priority = priority;
    }

    public void setConnecting(boolean connecting) {
        this.connecting = connecting;
    }

    public boolean isConnecting() {
        return connecting;
    }

    public UUID getUuid() {
        return uuid;
    }

    public ProxiedPlayer getProxiedPlayer() {
        return ProxyServer.getInstance().getPlayer(uuid);
    }

    public ServerInfo getTargetServer() {
        return targetServer;
    }

    public void setTargetServer(ServerInfo targetServer) {
        this.targetServer = targetServer;
    }

    public boolean isWaiting() {
        return this.targetServer != null;
    }

    public Priority getPriority() {
        return priority;
    }

    public static Optional<QueuedPlayer> get(ProxiedPlayer proxiedPlayer) {
        for(QueuedPlayer queuedPlayer : queuedPlayerSet) {
            if(queuedPlayer.getProxiedPlayer().equals(proxiedPlayer)) {
                return Optional.of(queuedPlayer);
            }
        }
        return Optional.empty();
    }

    public static Optional<QueuedPlayer> get(UUID uuid) {
        for(QueuedPlayer queuedPlayer : queuedPlayerSet) {
            if(queuedPlayer.getProxiedPlayer().getUniqueId().equals(uuid)) {
                return Optional.of(queuedPlayer);
            }
        }
        return Optional.empty();
    }

    public static Set<QueuedPlayer> getQueuedPlayerSet() {
        return queuedPlayerSet;
    }
}
