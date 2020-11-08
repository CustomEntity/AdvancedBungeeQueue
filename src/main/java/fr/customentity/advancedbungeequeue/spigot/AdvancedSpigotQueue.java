package fr.customentity.advancedbungeequeue.spigot;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.*;

public class AdvancedSpigotQueue extends JavaPlugin implements Listener {

    private SocketManager socketManager;
    private Set<UUID> waitingConnections = Collections.synchronizedSet(new HashSet<>());

    public void onEnable() {
        saveDefaultConfig();
        Bukkit.getPluginManager().registerEvents(this, this);

        this.socketManager = new SocketManager(this);
        this.socketManager.initListener();
    }

    public void onDisable() {
        try {
            socketManager.getSocket().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if(waitingConnections.contains(player.getUniqueId())) {
            this.socketManager.sendConnectedMessage(player.getUniqueId());
        }
    }

    public Set<UUID> getWaitingConnections() {
        return waitingConnections;
    }

    public SocketManager getSocketManager() {
        return socketManager;
    }

}
