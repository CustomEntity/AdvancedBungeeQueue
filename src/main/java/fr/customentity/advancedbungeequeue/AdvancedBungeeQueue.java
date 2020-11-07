package fr.customentity.advancedbungeequeue;

import fr.customentity.advancedbungeequeue.command.QueueCommand;
import fr.customentity.advancedbungeequeue.i18n.I18n;
import fr.customentity.advancedbungeequeue.i18n.YamlResourceBundle;
import fr.customentity.advancedbungeequeue.manager.QueueManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import javax.xml.soap.Text;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class AdvancedBungeeQueue extends Plugin {

    private QueueManager queueManager;

    private File file;
    private Configuration configFile;

    private I18n i18n;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.i18n = new I18n(this, configFile.getString("locale"));

        this.queueManager = new QueueManager(this);
        this.getProxy().getPluginManager().registerCommand(this, new QueueCommand(this));
    }

    public I18n getI18n() {
        return i18n;
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

    }

    public Configuration getConfigFile() {
        return configFile;
    }

    public QueueManager getQueueManager() {
        return queueManager;
    }

    public void sendConfigMessage(ProxiedPlayer player, String path, String... replace) {
        AdvancedBungeeQueue advancedBungeeQueue = (AdvancedBungeeQueue) ProxyServer.getInstance().getPluginManager().getPlugin("AdvancedBungeeQueue");
        HashMap<String, String> replaced = new HashMap<>();
        List<String> replaceList = Arrays.asList(replace);
        int index = 0;
        for (String str : replaceList) {
            index++;
            if (index % 2 == 0) continue;
            replaced.put(str, replaceList.get(index));
        }
        Optional<YamlResourceBundle> yamlResourceBundleOptional = i18n.getYamlResourceBundleByLang(getConfigFile().getString("locale"));
        if(yamlResourceBundleOptional.isPresent()) {
            YamlResourceBundle yamlResourceBundle = yamlResourceBundleOptional.get();
            List<String> messages = yamlResourceBundle.getYamlConfig().get(path) instanceof List ? yamlResourceBundle.getStringList(path) : Collections.singletonList(yamlResourceBundle.getString(path));

           messages.forEach(s -> sendConfigMessage(player, s, replaced));
        }
    }

    private void sendConfigMessage(ProxiedPlayer player, String configMessage, HashMap<String, String> replaced) {
        String message = ChatColor.translateAlternateColorCodes('&', configMessage.replace("%name%", player.getName()).replace("%prefix%", i18n.getString("prefix")));
        if (message.isEmpty()) return;

        for (Map.Entry<String, String> stringEntry : replaced.entrySet()) {
            message = message.replace(stringEntry.getKey(), stringEntry.getValue());
        }
        if (message.toLowerCase().startsWith("%title%")) {
            Title title = ProxyServer.getInstance().createTitle();

            title.fadeIn(0);
            title.fadeOut(10);
            title.stay(40);

            if (message.toLowerCase().contains("%subtitle%")) {
                String[] splitted = message.split("%subtitle%");
                title.title(new TextComponent(splitted[0].replaceAll("(?i)%subtitle%", "").replaceAll("(?i)%title%", "")));
                title.subTitle(new TextComponent(splitted[1].replaceAll("(?i)%subtitle%", "").replaceAll("(?i)%title%", "")));
                title.send(player);
            } else {
                title.title(new TextComponent(message.replaceAll("(?i)%title%", "")));
            }
        } else if (message.toLowerCase().startsWith("%subtitle%")) {
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
            }
        } else if (message.toLowerCase().startsWith("%actionbar%")) {
            player.sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message.replaceAll("(?i)%actionbar%", "")));
        } else {
            player.sendMessage(new TextComponent(message));
        }
    }
}
