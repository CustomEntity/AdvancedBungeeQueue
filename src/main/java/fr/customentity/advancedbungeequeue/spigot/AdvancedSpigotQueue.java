package fr.customentity.advancedbungeequeue.spigot;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import fr.customentity.advancedbungeequeue.common.QueueResult;
import fr.customentity.advancedbungeequeue.bungee.i18n.I18n;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class AdvancedSpigotQueue extends JavaPlugin implements Listener {

    private SocketManager socketManager;

    public void onEnable() {
        saveDefaultConfig();

        Bukkit.getPluginManager().registerEvents(this, this);

        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "AdvancedBungeeQueue");
        this.socketManager = new SocketManager(this);
    }

    public void onDisable() {
        try {
            if(socketManager != null && socketManager.getSocket() != null)
                socketManager.getSocket().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        this.socketManager.sendConnectionConfirmation(player.getUniqueId(), QueueResult.valueOf(event.getResult().name()));
    }

    public SocketManager getSocketManager() {
        return socketManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 0) {

        } else {
            if(args[0].equalsIgnoreCase("join")) {
                if(!(sender instanceof Player))return true;
                Player player = (Player)sender;
                if(args.length != 2) {
                    return true;
                }
                String serverName = args[1];
                this.socketManager.sendCommandToExecute(player.getUniqueId(), "queue join " + serverName);
            }
        }
        return false;
    }
}
