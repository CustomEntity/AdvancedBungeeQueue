package fr.customentity.advancedbungeequeue.bungee.socket;

import fr.customentity.advancedbungeequeue.bungee.AdvancedBungeeQueue;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

public class SocketManager {

    ServerSocket socket;
    private AdvancedBungeeQueue plugin;

    private ExecutorService cachedExecutorService;

    public SocketManager(AdvancedBungeeQueue plugin) {
        this.plugin = plugin;

        this.cachedExecutorService = Executors.newCachedThreadPool();
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

                        this.cachedExecutorService.submit(new ServerThread(plugin, client));
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
