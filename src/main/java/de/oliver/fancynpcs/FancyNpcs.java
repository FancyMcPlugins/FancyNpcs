package de.oliver.fancynpcs;

import de.oliver.fancylib.FancyLib;
import de.oliver.fancylib.LanguageConfig;
import de.oliver.fancylib.Metrics;
import de.oliver.fancylib.VersionFetcher;
import de.oliver.fancylib.serverSoftware.ServerSoftware;
import de.oliver.fancylib.serverSoftware.schedulers.BukkitScheduler;
import de.oliver.fancylib.serverSoftware.schedulers.FancyScheduler;
import de.oliver.fancylib.serverSoftware.schedulers.FoliaScheduler;
import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcData;
import de.oliver.fancynpcs.api.NpcManager;
import de.oliver.fancynpcs.commands.FancyNpcsCMD;
import de.oliver.fancynpcs.commands.npc.NpcCMD;
import de.oliver.fancynpcs.listeners.PlayerJoinListener;
import de.oliver.fancynpcs.listeners.PlayerQuitListener;
import de.oliver.fancynpcs.listeners.PlayerUseUnknownEntityListener;
import de.oliver.fancynpcs.tracker.NpcTracker;
import de.oliver.fancynpcs.v1_19_4.Npc_1_19_4;
import de.oliver.fancynpcs.v1_20_1.Npc_1_20_1;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

public class FancyNpcs extends JavaPlugin implements FancyNpcsPlugin {

    public static final String[] SUPPORTED_VERSIONS = new String[]{"1.19.4", "1.20.1"};

    private static FancyNpcs instance;
    private final FancyScheduler scheduler;
    private final FancyNpcConfig config;
    private final LanguageConfig languageConfig;
    private final VersionFetcher versionFetcher;
    private NpcManagerImpl npcManager;
    private Function<NpcData, Npc> npcAdapter;
    private boolean usingPlaceholderAPI;

    public FancyNpcs() {
        instance = this;
        this.scheduler = ServerSoftware.isFolia()
                ? new FoliaScheduler(instance)
                : new BukkitScheduler(instance);
        this.config = new FancyNpcConfig();
        this.languageConfig = new LanguageConfig(this);
        this.versionFetcher = new VersionFetcher("https://api.modrinth.com/v2/project/fancynpcs/version", "https://modrinth.com/plugin/fancynpcs/versions");
    }

    public static FancyNpcs getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        String mcVersion = Bukkit.getMinecraftVersion();

        switch (mcVersion) {
            case "1.20.1" -> npcAdapter = Npc_1_20_1::new;
            case "1.19.4" -> npcAdapter = Npc_1_19_4::new;
            default -> npcAdapter = null;
        }

        PluginManager pluginManager = Bukkit.getPluginManager();

        if (npcAdapter == null) {
            getLogger().warning("--------------------------------------------------");
            getLogger().warning("Unsupported minecraft server version.");
            getLogger().warning("Please update the server to " + String.join(" / ", SUPPORTED_VERSIONS) + ".");
            getLogger().warning("Disabling the FancyNpcs plugin.");
            getLogger().warning("--------------------------------------------------");
            pluginManager.disablePlugin(this);
            return;
        }

        npcManager = new NpcManagerImpl(this, npcAdapter);
        saveFile("lang.yml");
    }

    @Override
    public void onEnable() {
        if (npcAdapter == null) {
            return;
        }

        FancyLib.setPlugin(instance);
        config.reload();

        // Load language file
        String defaultLang = readResource("lang.yml");
        if (defaultLang != null) {
            // Update language file
            try {
                FileConfiguration defaultLangConfig = new YamlConfiguration();
                defaultLangConfig.loadFromString(defaultLang);
                for (String key : defaultLangConfig.getConfigurationSection("messages").getKeys(false)) {
                    languageConfig.addDefaultLang(key, defaultLangConfig.getString("messages." + key));
                }
            } catch (InvalidConfigurationException e) {
                e.printStackTrace();
            }
        }
        languageConfig.load();

        new Thread(() -> {
            ComparableVersion newestVersion = versionFetcher.getNewestVersion();
            ComparableVersion currentVersion = new ComparableVersion(getDescription().getVersion());
            if (newestVersion == null) {
                getLogger().warning("Could not fetch latest plugin version");
            } else if (newestVersion.compareTo(currentVersion) > 0) {
                getLogger().warning("-------------------------------------------------------");
                getLogger().warning("You are not using the latest version the FancyNpcs plugin.");
                getLogger().warning("Please update to the newest version (" + newestVersion + ").");
                getLogger().warning(versionFetcher.getDownloadUrl());
                getLogger().warning("-------------------------------------------------------");
            }
        }).start();

        if (!ServerSoftware.isPaper()) {
            getLogger().warning("--------------------------------------------------");
            getLogger().warning("It is recommended to use Paper as server software.");
            getLogger().warning("Because you are not using paper, the plugin");
            getLogger().warning("might not work correctly.");
            getLogger().warning("--------------------------------------------------");
        }

        // register bStats
        Metrics metrics = new Metrics(this, 17543);

        PluginManager pluginManager = Bukkit.getPluginManager();
        usingPlaceholderAPI = pluginManager.isPluginEnabled("PlaceholderAPI");

        // register commands
        getCommand("fancynpcs").setExecutor(new FancyNpcsCMD());
        getCommand("npc").setExecutor(new NpcCMD());

        // register listeners
        pluginManager.registerEvents(new PlayerJoinListener(), instance);
        pluginManager.registerEvents(new PlayerQuitListener(), instance);
        pluginManager.registerEvents(new PlayerUseUnknownEntityListener(), instance);

        // using bungee plugin channel
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        // load config
        scheduler.runTaskLater(null, 20L * 5, () -> npcManager.loadNpcs());

        scheduler.runTaskTimerAsynchronously(0, 1, new NpcTracker());

        int autosaveInterval = config.getAutoSaveInterval();
        if (config.isEnableAutoSave()) {
            scheduler.runTaskTimerAsynchronously(autosaveInterval * 60L * 20L, autosaveInterval * 60L * 20L, () -> npcManager.saveNpcs(false));
        }
    }

    @Override
    public void onDisable() {
        getServer().getMessenger().unregisterOutgoingPluginChannel(this);
        if (npcManager != null) {
            npcManager.saveNpcs(true);
        }
    }

    private String readResource(String name) {
        URL url = getClass().getClassLoader().getResource(name);
        if (url == null) {
            getLogger().severe(name + " not found");
            return null;
        }
        URLConnection connection = null;
        try {
            connection = url.openConnection();
        } catch (IOException e) {
            getLogger().severe("Failed unpack file " + name + ":" + e.getMessage());
        }
        connection.setUseCaches(false);
        try (InputStream inputStream = connection.getInputStream()) {
            byte[] file_raw = new byte[inputStream.available()];
            inputStream.read(file_raw);
            inputStream.close();
            return new String(file_raw, StandardCharsets.UTF_8);
        } catch (IOException e) {
            getLogger().severe("Failed read file " + name + ":" + e.getMessage());
        }
        return null;
    }

    private void saveFile(String name) {
        URL url = getClass().getClassLoader().getResource(name);
        if (url == null) {
            getLogger().severe(name + " not found");
            return;
        }
        File file = new File(getDataFolder() + "/" + name);
        if (file.exists()) return;
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        URLConnection connection = null;
        try {
            connection = url.openConnection();
        } catch (IOException e) {
            getLogger().severe("Failed unpack file " + name + ":" + e.getMessage());
        }
        connection.setUseCaches(false);
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            int read;
            InputStream inputStream = connection.getInputStream();
            byte[] bytes = new byte[1024];
            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        } catch (IOException e) {
            getLogger().severe("Failed unpack file " + name + ":" + e.getMessage());
        }
    }

    @Override
    public Function<NpcData, Npc> getNpcAdapter() {
        return npcAdapter;
    }

    @Override
    public FancyScheduler getScheduler() {
        return scheduler;
    }

    public NpcManagerImpl getNpcManagerImpl() {
        return npcManager;
    }

    @Override
    public NpcManager getNpcManager() {
        return npcManager;
    }

    public FancyNpcConfig getFancyNpcConfig() {
        return config;
    }

    public LanguageConfig getLanguageConfig() {
        return languageConfig;
    }

    public VersionFetcher getVersionFetcher() {
        return versionFetcher;
    }

    @Override
    public boolean isUsingPlaceholderAPI() {
        return usingPlaceholderAPI;
    }

    @Override
    public JavaPlugin getPlugin() {
        return instance;
    }
}
