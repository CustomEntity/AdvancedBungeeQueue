package fr.customentity.advancedbungeequeue.bungee.socket;

import fr.customentity.advancedbungeequeue.bungee.AdvancedBungeeQueue;
import fr.customentity.advancedbungeequeue.spigot.AdvancedSpigotQueue;
import net.md_5.bungee.api.ProxyServer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.UUID;
import java.util.logging.Level;

public class SocketManager {

    ServerSocket socket;
    private AdvancedBungeeQueue plugin;

    public SocketManager(AdvancedBungeeQueue plugin) {
        this.plugin = plugin;
    }

    public void initListener() {
        int port = plugin.getConfigFile().getInt("socket-port");
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
