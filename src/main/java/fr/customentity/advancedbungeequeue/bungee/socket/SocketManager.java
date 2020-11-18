package fr.customentity.advancedbungeequeue.bungee.socket;

import fr.customentity.advancedbungeequeue.bungee.AdvancedBungeeQueue;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;

public class SocketManager {

    ServerSocket socket;
    private AdvancedBungeeQueue plugin;

    public SocketManager(AdvancedBungeeQueue plugin) {
        this.plugin = plugin;
    }

    public void initListener() {
        int port = plugin.getConfigFile().getInt("socket-port", 1233);
        try {
            plugin.log(Level.INFO, "Trying to connect on port " + port + "...");
            socket = new ServerSocket(port);
            Thread thread = new Thread(() -> {
                try {
                    plugin.log(Level.INFO, "Connected on port " + port);
                    while (!socket.isClosed()) {
                        Socket client = socket.accept();
                        client.setKeepAlive(true);

                        ServerThread serverThread = new ServerThread(plugin, client);
                        serverThread.start();
                    }

                } catch (IOException e) {
                    if(e.getMessage().contains("Socket closed"))return;
                    e.printStackTrace();
                }
            });
            thread.start();
        } catch (IOException e) {
            plugin.log(Level.WARNING, "Cannot connect to port: " + port + "");
            e.printStackTrace();
        }
    }

    public ServerSocket getSocket() {
        return socket;
    }
}
