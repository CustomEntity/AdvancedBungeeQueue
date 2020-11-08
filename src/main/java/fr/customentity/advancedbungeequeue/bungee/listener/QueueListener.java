package fr.customentity.advancedbungeequeue.bungee.listener;

import fr.customentity.advancedbungeequeue.bungee.AdvancedBungeeQueue;
import fr.customentity.advancedbungeequeue.bungee.data.QueuedPlayer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Optional;

public class QueueListener implements Listener {

    private AdvancedBungeeQueue plugin;

    public QueueListener(AdvancedBungeeQueue plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent event) {
        ProxiedPlayer proxiedPlayer = event.getPlayer();
        Optional<QueuedPlayer> queuedPlayer = QueuedPlayer.get(proxiedPlayer);
        queuedPlayer.ifPresent(player -> plugin.getQueueManager().removePlayerFromQueue(player));
    }

    @EventHandler
    public void onSwitch(ServerSwitchEvent event) {
        ServerInfo from = event.getFrom();
        if(from == null)return;
        if (plugin.getConfigFile().getStringList("default-servers").stream().noneMatch(s -> from.getName().contains(s))) {
            return;
        }
        Optional<QueuedPlayer> queuedPlayer = QueuedPlayer.get(event.getPlayer());
        if(!queuedPlayer.isPresent())return;

        if(!queuedPlayer.get().isConnecting()) {
            plugin.getQueueManager().removePlayerFromQueue(queuedPlayer.get());
        }
    }
}
