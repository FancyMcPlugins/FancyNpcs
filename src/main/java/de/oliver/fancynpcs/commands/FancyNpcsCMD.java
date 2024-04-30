package de.oliver.fancynpcs.commands;

import de.oliver.fancylib.translations.Translator;
import de.oliver.fancylib.translations.message.SimpleMessage;
import de.oliver.fancynpcs.FancyNpcs;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

import org.jetbrains.annotations.NotNull;

public enum FancyNpcsCMD {
    INSTANCE; // SINGLETON

    private final FancyNpcs plugin = FancyNpcs.getInstance();
    private final Translator translator = FancyNpcs.getInstance().getTranslator();

    @Command("fancynpcs")
    @Permission("fancynpcs.command.fancynpcs")
    public void onDefault(final CommandSender sender) {
        translator.translate("fancynpcs_syntax").send(sender);
    }

    @Command("fancynpcs version")
    @Permission("fancynpcs.command.fancynpcs.version")
    public void onVersion(final CommandSender sender) {
        plugin.getVersionConfig().checkVersionAndDisplay(sender, false);
    }

    @Command("fancynpcs reload")
    @Permission("fancynpcs.command.fancynpcs.reload")
    public void onReload(final CommandSender sender) {
        // Reloading all defined languages.
        translator.loadLanguages(plugin.getDataFolder().getAbsolutePath());
        // Reloading configuration
        plugin.getFancyNpcConfig().reload();
        // Updating the selected language obtained from configuration.
        translator.setSelectedLanguage(translator.getFallbackLanguage()); // WIP: Make it configurable.
        // Reloading all NPCs.
        plugin.getNpcManagerImpl().reloadNpcs();
        // Sending success message to the sender.
        translator.translate("fancynpcs_reload_success").send(sender);
    }

    @Command("fancynpcs save")
    @Permission("fancynpcs.command.fancynpcs.save")
    public void onSave(final CommandSender sender) {
        // Saving all NPCs.
        plugin.getNpcManagerImpl().saveNpcs(true);
        // Sending success message to the sender.
        translator.translate("fancynpcs_save_success").send(sender);
    }

    @Command("fancynpcs feature_flags")
    @Permission("fancynpcs.command.fancynpcs.feature_flags")
    public void onFeatureFlags(final CommandSender sender) {
        // Printing the header of the list.
        translator.translate("fancynpcs_feature_flags_header").send(sender);
        // Printing status of all existing feature flags.
        translator.translate("fancynpcs_feature_flags_entry")
                .replace("number", "1")
                .replace("name", "Player NPCs")
                .replace("id", FancyNpcs.PLAYER_NPCS_FEATURE_FLAG.getName())
                .replace("state", getTranslatedState(FancyNpcs.PLAYER_NPCS_FEATURE_FLAG.isEnabled()))
                .send(sender);
        // Printing the footer of the list.
        translator.translate("fancynpcs_feature_flags_footer").send(sender);
    }

    // NOTE: Might need to be improved later down the line, should get work done for now.
    private @NotNull String getTranslatedState(final boolean bool) {
        return (bool) ? ((SimpleMessage) translator.translate("enabled")).getMessage() : ((SimpleMessage) translator.translate("disabled")).getMessage();
    }

}
