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
                if (!sender.hasPermission(plugin.getConfigFile().getString("permissions.join-queue-command"))) {
                    plugin.sendConfigMessage(sender, "commands.no-permission");
                    return;
                }
                if (!(sender instanceof ProxiedPlayer)) return;
                ProxiedPlayer proxiedPlayer = (ProxiedPlayer) sender;
                if (args.length != 2) {
                    plugin.sendConfigMessage(proxiedPlayer, "commands.join-command.syntax");
                    return;
                }
                String serverName = args[1];
                ServerInfo serverInfo = plugin.getProxy().getServerInfo(serverName);
                if (serverInfo == null) {
                    plugin.sendConfigMessage(proxiedPlayer, "commands.join-command.server-doesnt-exist");
                    return;
                }

                if(!this.plugin.getConfigFile().getStringList("queued-servers").contains(serverInfo.getName())) {
                    plugin.sendConfigMessage(proxiedPlayer, "commands.join-command.server-not-queued");
                    return;
                }

                plugin.getQueueManager().addPlayerInQueue(proxiedPlayer, serverInfo);
            } else if (args[0].equalsIgnoreCase("on")) {
                if (!sender.hasPermission(plugin.getConfigFile().getString("permissions.on-queue-command"))) {
                    plugin.sendConfigMessage(sender, "commands.no-permission");
                    return;
                }
                if (plugin.getQueueManager().isEnabled()) {
                    plugin.sendConfigMessage(sender, "commands.on-command.error");
                    return;
                }
                plugin.getQueueManager().setEnabled(true);
                plugin.sendConfigMessage(sender, "commands.on-command.success");
            } else if (args[0].equalsIgnoreCase("off")) {
                if (!sender.hasPermission(plugin.getConfigFile().getString("permissions.off-queue-command"))) {
                    plugin.sendConfigMessage(sender, "commands.no-permission");
                    return;
                }
                if (!plugin.getQueueManager().isEnabled()) {
                    plugin.sendConfigMessage(sender, "commands.off-command.error");
                    return;
                }
                plugin.getQueueManager().setEnabled(false);
                plugin.sendConfigMessage(sender, "commands.off-command.success");
            } else if (args[0].equalsIgnoreCase("pause")) {
                if (!sender.hasPermission(plugin.getConfigFile().getString("permissions.pause-queue-command"))) {
                    plugin.sendConfigMessage(sender, "commands.no-permission");
                    return;
                }
                if (plugin.getQueueManager().isPaused()) {
                    plugin.sendConfigMessage(sender, "commands.pause-command.error");
                    return;
                }
                plugin.getQueueManager().setPaused(true);
                plugin.sendConfigMessage(sender, "commands.pause-command.success");
            } else if (args[0].equalsIgnoreCase("unpause")) {
                if (!sender.hasPermission(plugin.getConfigFile().getString("permissions.unpause-queue-command"))) {
                    plugin.sendConfigMessage(sender, "commands.no-permission");
                    return;
                }
                if (!plugin.getQueueManager().isPaused()) {
                    plugin.sendConfigMessage(sender, "commands.unpause-command.error");
                    return;
                }
                plugin.getQueueManager().setPaused(false);
                plugin.sendConfigMessage(sender, "commands.unpause-command.success");
            }
        }
    }
}
