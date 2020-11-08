package fr.customentity.advancedbungeequeue.bungee.command;

import fr.customentity.advancedbungeequeue.bungee.AdvancedBungeeQueue;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class QueueCommand extends Command {

    private AdvancedBungeeQueue plugin;

    public QueueCommand(AdvancedBungeeQueue plugin) {
        super("queue", null, "advancedbungeequeue");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            plugin.sendConfigMessage(sender, "help");
        } else {
            if (args[0].equalsIgnoreCase("join")) {
                if(!sender.hasPermission(plugin.getConfigFile().getString("permissions.join-queue-fr.customentity.advancedbungeequeue.command"))) {
                    plugin.sendConfigMessage(sender, "commands.no-permission");
                    return;
                }
                if (!(sender instanceof ProxiedPlayer)) return;
                ProxiedPlayer proxiedPlayer = (ProxiedPlayer) sender;
                if (args.length != 2) {
                    plugin.sendConfigMessage(proxiedPlayer, "commands.join.syntax");
                    return;
                }
                String serverName = args[1];
                ServerInfo serverInfo = plugin.getProxy().getServerInfo(serverName);
                if (serverInfo == null) {
                    plugin.sendConfigMessage(proxiedPlayer, "commands.join.server-doesnt-exist");
                    return;
                }

                plugin.getQueueManager().addPlayerInQueue(proxiedPlayer, serverInfo);
            } else if(args[0].equalsIgnoreCase("on")) {
                if(!sender.hasPermission(plugin.getConfigFile().getString("permissions.on-queue-fr.customentity.advancedbungeequeue.command"))) {
                    plugin.sendConfigMessage(sender, "commands.no-permission");
                    return;
                }
                if(plugin.getQueueManager().isEnabled()) {
                    plugin.sendConfigMessage(sender, "commands.on.error");
                    return;
                }
                plugin.getQueueManager().setEnabled(true);
                plugin.sendConfigMessage(sender, "commands.on.success");
            } else if(args[0].equalsIgnoreCase("off")) {
                if(!sender.hasPermission(plugin.getConfigFile().getString("permissions.off-queue-fr.customentity.advancedbungeequeue.command"))) {
                    plugin.sendConfigMessage(sender, "commands.no-permission");
                    return;
                }
                if(!plugin.getQueueManager().isEnabled()) {
                    plugin.sendConfigMessage(sender, "commands.off.error");
                    return;
                }
                plugin.getQueueManager().setEnabled(false);
                plugin.sendConfigMessage(sender, "commands.off.success");
            } else if(args[0].equalsIgnoreCase("pause")) {
                if(!sender.hasPermission(plugin.getConfigFile().getString("permissions.pause-queue-fr.customentity.advancedbungeequeue.command"))) {
                    plugin.sendConfigMessage(sender, "commands.no-permission");
                    return;
                }
                if(plugin.getQueueManager().isPaused()) {
                    plugin.sendConfigMessage(sender, "commands.pause.error");
                    return;
                }
                plugin.getQueueManager().setPaused(true);
                plugin.sendConfigMessage(sender, "commands.pause.success");
            }else if(args[0].equalsIgnoreCase("unpause")) {
                if(!sender.hasPermission(plugin.getConfigFile().getString("permissions.unpause-queue-fr.customentity.advancedbungeequeue.command"))) {
                    plugin.sendConfigMessage(sender, "commands.no-permission");
                    return;
                }
                if(!plugin.getQueueManager().isPaused()) {
                    plugin.sendConfigMessage(sender, "commands.unpause.error");
                    return;
                }
                plugin.getQueueManager().setPaused(false);
                plugin.sendConfigMessage(sender, "commands.unpause.success");
            }
        }
    }
}
