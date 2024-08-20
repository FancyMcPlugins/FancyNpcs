package de.oliver.fancynpcs.api;

import de.oliver.fancylib.serverSoftware.schedulers.FancyScheduler;
import de.oliver.fancylib.translations.Translator;
import de.oliver.fancynpcs.api.utils.SkinCache;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Function;

public interface FancyNpcsPlugin {

    static FancyNpcsPlugin get() {
        PluginManager pluginManager = Bukkit.getPluginManager();

        if (pluginManager.isPluginEnabled("FancyNpcs")) {
            return (FancyNpcsPlugin) pluginManager.getPlugin("FancyNpcs");
        }

        throw new NullPointerException("Plugin is not enabled");
    }

    JavaPlugin getPlugin();

    ScheduledExecutorService getNpcThread();

    FancyScheduler getScheduler();

    Function<NpcData, Npc> getNpcAdapter();

    NpcManager getNpcManager();

    FancyNpcsConfig getFancyNpcConfig();

    AttributeManager getAttributeManager();

    Translator getTranslator();

    SkinCache getSkinCache();
}
