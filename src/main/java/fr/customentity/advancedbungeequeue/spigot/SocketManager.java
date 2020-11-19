package fr.customentity.advancedbungeequeue.spigot;

import fr.customentity.advancedbungeequeue.common.QueueResult;
import fr.customentity.advancedbungeequeue.common.actions.all.ConfirmConnectionAction;
import fr.customentity.advancedbungeequeue.common.actions.all.ExecuteCommandAction;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
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

    public void sendConnectionConfirmation(UUID uuid, QueueResult result) {
        Thread thread = new Thread(() -> {
            Socket client;
            try {
                client = new Socket(plugin.getConfig().getString("socket-host", "localhost"), plugin.getConfig().getInt("socket-port", 1233));
                OutputStream out = client.getOutputStream();
                ObjectOutputStream writer = new ObjectOutputStream(out);
                writer.writeUTF(plugin.getConfig().getString("socket-password", "TOABSOLUTELYCHANGE"));
                writer.writeObject(new ConfirmConnectionAction(uuid, result));
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

    public void sendCommandToExecute(UUID uuid, String command) {
        Thread thread = new Thread(() -> {
            Socket client;
            try {
                client = new Socket(plugin.getConfig().getString("socket-host", "localhost"), plugin.getConfig().getInt("socket-port"));
                OutputStream out = client.getOutputStream();
                ObjectOutputStream writer = new ObjectOutputStream(out);
                writer.writeUTF(plugin.getConfig().getString("socket-password", "TOABSOLUTELYCHANGE"));
                writer.writeObject(new ExecuteCommandAction(uuid, command));
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
