package fr.customentity.advancedbungeequeue.bungee;

import fr.customentity.advancedbungeequeue.bungee.command.QueueCommand;
import fr.customentity.advancedbungeequeue.bungee.i18n.I18n;
import fr.customentity.advancedbungeequeue.bungee.i18n.YamlResourceBundle;
import fr.customentity.advancedbungeequeue.bungee.listener.QueueListener;
import fr.customentity.advancedbungeequeue.bungee.manager.QueueManager;
import fr.customentity.advancedbungeequeue.bungee.socket.SocketManager;
import net.md_5.bungee.api.*;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AdvancedBungeeQueue extends Plugin {

    private QueueManager queueManager;

    private File file;
    private Configuration configFile;
    private SocketManager socketManager;

    private I18n i18n;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.i18n = new I18n(this, configFile.getString("locale"));

        this.queueManager = new QueueManager(this);
        this.getProxy().getPluginManager().registerCommand(this, new QueueCommand(this));
        this.getProxy().getPluginManager().registerListener(this, new QueueListener(this));

        this.getProxy().registerChannel("AdvancedBungeeQueue");

        this.getProxy().getScheduler().schedule(this, () -> queueManager.getQueues().forEach((serverInfo, queuedPlayers) -> queuedPlayers.forEach(queuedPlayer -> this.sendConfigMessage(queuedPlayer.getProxiedPlayer(), "general.repeating-position-message",
                "%all%", queuedPlayers.size() + "",
                "%position%", queuedPlayers.indexOf(queuedPlayer) + 1 + "",
                "%priority%", queuedPlayer.getPriority().getName()
        ))), 1, 1, TimeUnit.SECONDS);

        this.socketManager = new SocketManager(this);
        this.socketManager.initListener();
    }

    public SocketManager getSocketManager() {
        return socketManager;
    }

    public void log(Level level, String message) {
        Logger.getLogger(this.getDescription().getName()).log(level, message);
    }

    public I18n getI18n() {
        return i18n;
    }

    private InputStream getResource(String filename) {
        if (filename == null) {
            throw new IllegalArgumentException("Filename cannot be null");
        }

        try {
            URL url = this.getClass().getClassLoader().getResource(filename);

            if (url == null) {
                return null;
            }

            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            return connection.getInputStream();
        } catch (IOException ex) {
            return null;
        }
    }

    public void saveResource(String resourcePath, boolean replace) {
        if (resourcePath == null || resourcePath.equals("")) return;

        resourcePath = resourcePath.replace('\\', '/');
        InputStream in = getResource(resourcePath);
        if (in == null) return;

        File outFile = new File(this.getDataFolder(), resourcePath);
        int lastIndex = resourcePath.lastIndexOf('/');
        File outDir = new File(this.getDataFolder(), resourcePath.substring(0, Math.max(lastIndex, 0)));

        if (!outDir.exists()) {
            outDir.mkdirs();
        }

        try {
            if (!outFile.exists() || replace) {
                OutputStream out = new FileOutputStream(outFile);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.close();
                in.close();
            }
        } catch (IOException ignore) { }
    }

    private void saveDefaultConfig() {
        try {
            if (!this.getDataFolder().exists())
                this.getDataFolder().mkdirs();

            file = new File(this.getDataFolder(), "config.yml");
            if (!file.exists()) {
                Files.copy(getResourceAsStream("config.yml"), file.toPath());
            }
            configFile = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        try {
            this.getSocketManager().getSocket().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Configuration getConfigFile() {
        return configFile;
    }

    public QueueManager getQueueManager() {
        return queueManager;
    }

    public void sendConfigMessage(CommandSender sender, String path, String... replace) {
        HashMap<String, String> replaced = new HashMap<>();
        List<String> replaceList = Arrays.asList(replace);
        int index = 0;
        for (String str : replaceList) {
            index++;
            if (index % 2 == 0) continue;
            replaced.put(str, replaceList.get(index));
        }
        Optional<YamlResourceBundle> yamlResourceBundleOptional = i18n.getYamlResourceBundleByLang(getConfigFile().getString("locale"));
        if (yamlResourceBundleOptional.isPresent()) {
            YamlResourceBundle yamlResourceBundle = yamlResourceBundleOptional.get();
            List<String> messages = yamlResourceBundle.getYamlConfig().get(path) instanceof List ? yamlResourceBundle.getStringList(path) : Collections.singletonList(yamlResourceBundle.getString(path));

            messages.forEach(s -> sendConfigMessage(sender, s, replaced));
        }
    }

    private void sendConfigMessage(CommandSender sender, String configMessage, HashMap<String, String> replaced) {
        String message = ChatColor.translateAlternateColorCodes('&', configMessage.replace("%name%", sender.getName()).replace("%prefix%", i18n.getString("prefix")));
        if (message.isEmpty()) return;

        for (Map.Entry<String, String> stringEntry : replaced.entrySet()) {
            message = message.replace(stringEntry.getKey(), stringEntry.getValue());
        }
        if (message.toLowerCase().startsWith("%title%")) {
            if(!(sender instanceof ProxiedPlayer))return;
            ProxiedPlayer player = (ProxiedPlayer)sender;
            Title title = ProxyServer.getInstance().createTitle();

            title.fadeIn(0);
            title.fadeOut(10);
            title.stay(40);

            if (message.toLowerCase().contains("%subtitle%")) {
                String[] splitted = message.split("%subtitle%");
                title.title(new TextComponent(splitted[0].replaceAll("(?i)%subtitle%", "").replaceAll("(?i)%title%", "")));
                title.subTitle(new TextComponent(splitted[1].replaceAll("(?i)%subtitle%", "").replaceAll("(?i)%title%", "")));
            } else {
                title.title(new TextComponent(message.replaceAll("(?i)%title%", "")));
                title.subTitle(new TextComponent(""));
            }
            title.send(player);
        } else if (message.toLowerCase().startsWith("%subtitle%")) {
            if(!(sender instanceof ProxiedPlayer))return;
            ProxiedPlayer player = (ProxiedPlayer)sender;
            Title title = ProxyServer.getInstance().createTitle();

            title.fadeIn(0);
            title.fadeOut(10);
            title.stay(40);
            if (message.toLowerCase().contains("%title%")) {
                String[] splitted = message.split("%title%");
                title.title(new TextComponent(splitted[1].replaceAll("(?i)%subtitle%", "").replaceAll("(?i)%title%", "")));
                title.subTitle(new TextComponent(splitted[0].replaceAll("(?i)%subtitle%", "").replaceAll("(?i)%title%", "")));
            } else {
                title.subTitle(new TextComponent(message.replaceAll("(?i)%subtitle%", "")));
                title.title(new TextComponent(""));
            }
            title.send(player);
        } else if (message.toLowerCase().startsWith("%actionbar%")) {
            if(!(sender instanceof ProxiedPlayer))return;
            ProxiedPlayer player = (ProxiedPlayer)sender;
            player.sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message.replaceAll("(?i)%actionbar%", "")));
        } else {
            sender.sendMessage(new TextComponent(message));
        }
    }

}
