package fr.customentity.advancedbungeequeue.command;

import fr.customentity.advancedbungeequeue.AdvancedBungeeQueue;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class QueueCommand extends Command {

    private AdvancedBungeeQueue plugin;

    public QueueCommand(AdvancedBungeeQueue plugin) {
        super("queue", plugin.getConfigFile().getString("permissions.join-queue-command"), "advancedbungeequeue");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            //TODO: SEND MESSAGE
        } else {
            if (args[0].equalsIgnoreCase("join")) {
                if (!(sender instanceof ProxiedPlayer)) return;
                ProxiedPlayer proxiedPlayer = (ProxiedPlayer) sender;
                if (args.length != 2) {
                    //TODO: SEND MESSAGE
                    return;
                }
                String serverName = args[1];
                ServerInfo serverInfo = plugin.getProxy().getServerInfo(serverName);
                if (serverInfo == null) {
                    //TODO: SEND MESSAGE
                    return;
                }

                plugin.getQueueManager().addPlayerInQueue(proxiedPlayer, serverInfo);
            } else if(args[0].equalsIgnoreCase("on")) {

            } else if(args[0].equalsIgnoreCase("off")) {

            }
        }
    }
}
