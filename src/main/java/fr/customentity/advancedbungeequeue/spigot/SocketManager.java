package fr.customentity.advancedbungeequeue.spigot;

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

    public void initListener() {
        int port = plugin.getConfig().getInt("socket-port");
        try {
            plugin.getLogger().log(Level.INFO, "Trying to connect on port " + port + "...");
            socket = new ServerSocket(port);
            Thread thread = new Thread(() -> {
                try {
                    while (!socket.isClosed()) {
                        Socket client = socket.accept();
                        client.setKeepAlive(true);

                        new ServerThread(plugin, client);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            thread.start();
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "Cannot connect to port: " + port + "");
            e.printStackTrace();
        }
    }

    public void sendConnectedMessage(UUID uuid) {
        Thread thread = new Thread(() -> {
            Socket client;
            try {
                client = new Socket("localhost", plugin.getConfig().getInt("socket-port"));
                OutputStream out = client.getOutputStream();
                PrintWriter writer = new PrintWriter(out);
                writer.write("bungee");
                writer.write(uuid.toString());
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
