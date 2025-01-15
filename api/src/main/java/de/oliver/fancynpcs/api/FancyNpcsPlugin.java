package de.oliver.fancynpcs.api;

import de.oliver.fancyanalytics.logger.ExtendedFancyLogger;
import de.oliver.fancylib.serverSoftware.schedulers.FancyScheduler;
import de.oliver.fancylib.translations.Translator;
import de.oliver.fancynpcs.api.actions.ActionManager;
import de.oliver.fancynpcs.api.skins.SkinManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;

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

    ExtendedFancyLogger getFancyLogger();

    ScheduledExecutorService getNpcThread();

    /**
     *  Creates a new thread with the given name and runnable.
     *  Warning: Do not use this method, it is for internal use only.
     */
    @ApiStatus.Internal
    Thread newThread(String name, Runnable runnable);

    FancyScheduler getScheduler();

    Function<NpcData, Npc> getNpcAdapter();

    FancyNpcsConfig getFancyNpcConfig();

    NpcManager getNpcManager();

    AttributeManager getAttributeManager();

    ActionManager getActionManager();

    SkinManager getSkinManager();

    Translator getTranslator();
}
