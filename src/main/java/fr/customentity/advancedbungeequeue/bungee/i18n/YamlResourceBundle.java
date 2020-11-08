package fr.customentity.advancedbungeequeue.bungee.i18n;

import com.google.common.base.Charsets;
import fr.customentity.advancedbungeequeue.bungee.AdvancedBungeeQueue;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class YamlResourceBundle {

    private AdvancedBungeeQueue plugin;

    private Locale locale;

    private String bundleBaseName;

    private ConcurrentMap<String, List<String>> cachedResourceContent;

    private Configuration yamlConfig;

    private File yamlFile;

    private String fileName;

    public YamlResourceBundle(AdvancedBungeeQueue plugin, Locale locale, String bundleBaseName) {
        this.plugin = plugin;
        this.locale = locale;
        this.bundleBaseName = bundleBaseName;
        this.cachedResourceContent = new ConcurrentHashMap<>();
        this.fileName = this.bundleBaseName + "_" + locale.toLanguageTag().replace("-", "_") + ".yml";

        this.setupYamlResource();
    }

    private void setupYamlResource() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }
        try {
            yamlFile = new File(plugin.getDataFolder(), this.fileName);
            if (!yamlFile.exists()) {
                Files.copy(plugin.getResourceAsStream(this.fileName), yamlFile.toPath());
            }
            yamlConfig = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new InputStreamReader(new FileInputStream(yamlFile), Charsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void refreshCache() throws IOException {
        yamlConfig = ConfigurationProvider.getProvider(YamlConfiguration.class).load(yamlFile);
        cachedResourceContent.forEach((s, o) -> cachedResourceContent.put(s, yamlConfig.getStringList(s)));
    }

    public void clearCache() {
        this.cachedResourceContent.clear();
    }

    public String getString(String path) {
        return this.getString(path, true);
    }

    public List<String> getStringList(String path) {
        return this.getStringList(path, true);
    }

    public String getString(String path, boolean useCacheIfPresent) {
        if (useCacheIfPresent)
            return cachedResourceContent.getOrDefault(path, Collections.singletonList(yamlConfig.getString(path))).get(0);
        return yamlConfig.getString(path);
    }

    public List<String> getStringList(String path, boolean useCacheIfPresent) {
        if (useCacheIfPresent) return cachedResourceContent.getOrDefault(path, yamlConfig.getStringList(path));
        return yamlConfig.getStringList(path);
    }

    public Configuration getYamlConfig() {
        return yamlConfig;
    }

    public File getYamlFile() {
        return yamlFile;
    }

    public String getBundleBaseName() {
        return bundleBaseName;
    }

    public Locale getLocale() {
        return locale;
    }

    public ConcurrentMap<String, List<String>> getCachedResourceContent() {
        return cachedResourceContent;
    }
}
