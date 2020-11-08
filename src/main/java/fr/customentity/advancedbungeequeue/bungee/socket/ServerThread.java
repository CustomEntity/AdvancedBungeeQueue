package fr.customentity.advancedbungeequeue.bungee.socket;

import fr.customentity.advancedbungeequeue.bungee.AdvancedBungeeQueue;
import fr.customentity.advancedbungeequeue.bungee.data.QueuedPlayer;
import fr.customentity.advancedbungeequeue.spigot.AdvancedSpigotQueue;
import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Optional;
import java.util.UUID;

public class ServerThread extends Thread {

    private AdvancedBungeeQueue plugin;
    private Socket client;

    public ServerThread(AdvancedBungeeQueue plugin, Socket client) {
        this.plugin = plugin;
        this.client = client;
    }

    @Override
    public void run() {
        try {
            InputStream in = client.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String port = reader.readLine();
            if(port.equalsIgnoreCase("bungee")) {
                UUID uuid = UUID.fromString(reader.readLine());
                Optional<QueuedPlayer> queuedPlayer = QueuedPlayer.get(uuid);
                queuedPlayer.ifPresent(queuedPlayer1 -> plugin.getQueueManager().removePlayerFromQueue(queuedPlayer1));
            }

            in.close();
            reader.close();
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
