package fr.customentity.advancedbungeequeue.bungee.socket;

import fr.customentity.advancedbungeequeue.bungee.AdvancedBungeeQueue;
import fr.customentity.advancedbungeequeue.bungee.data.QueuedPlayer;
import fr.customentity.advancedbungeequeue.common.QueueResult;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
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
            ObjectInputStream reader = new ObjectInputStream(in);

            String passwd = reader.readUTF();
            if(plugin.getConfigFile().getString("socket-password").equals(passwd)) {
                UUID uuid = UUID.fromString(reader.readUTF());
                QueueResult result = QueueResult.valueOf(reader.readUTF());
                Optional<QueuedPlayer> queuedPlayer = QueuedPlayer.get(uuid);
                if (result == QueueResult.ALLOWED) {
                    queuedPlayer.ifPresent(queuedPlayer1 -> plugin.getQueueManager().removePlayerFromQueue(queuedPlayer1));
                } else if(result == QueueResult.KICK_WHITELIST) {
                    queuedPlayer.ifPresent(queuedPlayer1 -> plugin.sendConfigMessage(queuedPlayer1.getProxiedPlayer(), "general.kick-whitelisted"));
                } else if(result == QueueResult.KICK_FULL) {
                    queuedPlayer.ifPresent(queuedPlayer1 -> plugin.sendConfigMessage(queuedPlayer1.getProxiedPlayer(), "general.kick-full-server"));
                }else if(result == QueueResult.KICK_BANNED) {
                    queuedPlayer.ifPresent(queuedPlayer1 -> plugin.sendConfigMessage(queuedPlayer1.getProxiedPlayer(), "general.kick-banned"));
                } else if(result == QueueResult.KICK_OTHER) {
                    queuedPlayer.ifPresent(queuedPlayer1 -> plugin.sendConfigMessage(queuedPlayer1.getProxiedPlayer(), "general.kick-unavailable-server"));
                }
            }

            in.close();
            reader.close();
            client.close();

            this.interrupt();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
