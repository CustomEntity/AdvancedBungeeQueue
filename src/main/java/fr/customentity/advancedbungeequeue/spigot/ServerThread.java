package fr.customentity.advancedbungeequeue.spigot;

import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.UUID;

public class ServerThread extends Thread {

    private AdvancedSpigotQueue plugin;
    private Socket client;

    public ServerThread(AdvancedSpigotQueue plugin, Socket client) {
        this.plugin = plugin;
        this.client = client;
    }

    @Override
    public void run() {
        try {
            InputStream in = client.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String port = reader.readLine();
            if(port.equalsIgnoreCase("bungee"))return;
            int serverPort = Integer.parseInt(port);
            UUID uuid = UUID.fromString(reader.readLine());

            if (serverPort == Bukkit.getPort())
                plugin.getWaitingConnections().add(uuid);

            in.close();
            reader.close();
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
