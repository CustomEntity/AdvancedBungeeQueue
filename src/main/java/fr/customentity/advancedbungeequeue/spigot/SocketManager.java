package fr.customentity.advancedbungeequeue.spigot;

import fr.customentity.advancedbungeequeue.common.QueueResult;
import org.bukkit.event.player.PlayerLoginEvent;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.UUID;
import java.util.logging.Level;

public class SocketManager {

    ServerSocket socket;
    private AdvancedSpigotQueue plugin;

    public SocketManager(AdvancedSpigotQueue plugin) {
        this.plugin = plugin;
    }

    public void sendConnectedMessage(UUID uuid, QueueResult result) {
        Thread thread = new Thread(() -> {
            Socket client;
            try {
                client = new Socket("localhost", plugin.getConfig().getInt("socket-port"));
                OutputStream out = client.getOutputStream();
                ObjectOutputStream writer = new ObjectOutputStream(out);
                writer.writeUTF(plugin.getConfig().getString("socket-password", "TOABSOLUTELYCHANGE"));
                writer.writeUTF(uuid.toString());
                writer.writeUTF(result.name());
                writer.flush();
                writer.close();
                client.close();
            } catch (UnknownHostException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to connect to the server.");
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    public ServerSocket getSocket() {
        return socket;
    }
}
