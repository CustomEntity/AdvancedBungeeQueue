package fr.customentity.advancedbungeequeue.runnable;

import fr.customentity.advancedbungeequeue.AdvancedBungeeQueue;
import net.md_5.bungee.api.config.ServerInfo;

public class ConnectRunnable implements Runnable {

    private AdvancedBungeeQueue plugin;
    private ServerInfo serverInfo;

    public ConnectRunnable(AdvancedBungeeQueue plugin, ServerInfo serverInfo) {
        this.plugin = plugin;
        this.serverInfo = serverInfo;
    }

    @Override
    public void run() {
        if(!plugin.getQueueManager().isQueueRunning()) {
            return;
        }
        plugin.getQueueManager().connectNextPlayers(serverInfo);
    }
}
