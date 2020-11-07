package fr.customentity.advancedbungeequeue.data;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.*;

public class QueuedPlayer {

    private static Set<QueuedPlayer> queuedPlayerSet = Collections.synchronizedSet(new HashSet<>());

    private ProxiedPlayer proxiedPlayer;
    private ServerInfo targetServer;

    public QueuedPlayer(ProxiedPlayer proxiedPlayer) {
       this(proxiedPlayer, null);
    }

    public QueuedPlayer(ProxiedPlayer proxiedPlayer, ServerInfo targetServer) {
        this.proxiedPlayer = proxiedPlayer;
        this.targetServer = targetServer;

        queuedPlayerSet.add(this);
    }


    public ProxiedPlayer getProxiedPlayer() {
        return proxiedPlayer;
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

    public static QueuedPlayer get(ProxiedPlayer proxiedPlayer) {
        for(QueuedPlayer queuedPlayer : queuedPlayerSet) {
            if(queuedPlayer.getProxiedPlayer().equals(proxiedPlayer)) {
                return queuedPlayer;
            }
        }
        return new QueuedPlayer(proxiedPlayer);
    }
    public static Set<QueuedPlayer> getQueuedPlayerSet() {
        return queuedPlayerSet;
    }
}
