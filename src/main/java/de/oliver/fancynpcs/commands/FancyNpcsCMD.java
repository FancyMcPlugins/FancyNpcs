package de.oliver.fancynpcs.commands;

import de.oliver.fancylib.translations.Language;
import de.oliver.fancylib.translations.Translator;
import de.oliver.fancylib.translations.message.SimpleMessage;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.tests.impl.FancyNpcsTests;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.jetbrains.annotations.NotNull;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public enum FancyNpcsCMD {
    INSTANCE; // SINGLETON

    private final FancyNpcs plugin = FancyNpcs.getInstance();
    private final Translator translator = FancyNpcs.getInstance().getTranslator();
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Command("fancynpcs version")
    @Permission("fancynpcs.command.fancynpcs.version")
    public void onVersion(final CommandSender sender) {
        plugin.getVersionConfig().checkVersionAndDisplay(sender, false);
    }

    @Command("fancynpcs test")
    @Permission("fancynpcs.command.fancynpcs.test")
    public void onTest(final Player player) {
        FancyNpcsTests tests = new FancyNpcsTests();
        boolean tested = tests.runAllTests(player);

        if (tested) {
            translator.translate("fancynpcs_test_success")
                    .replace("player", player.getName())
                    .replace("time", dateTimeFormatter.format(new Date().toInstant().atZone(ZoneId.of("Europe/Berlin"))))
                    .replace("count", String.valueOf(tests.getTestCount()))
                    .send(player);
        } else {
            translator.translate("fancynpcs_test_failure")
                    .replace("player", player.getName())
                    .replace("time", dateTimeFormatter.format(new Date().toInstant().atZone(ZoneId.of("Europe/Berlin"))))
                    .send(player);
        }
    }

    @Command("fancynpcs reload")
    @Permission("fancynpcs.command.fancynpcs.reload")
    public void onReload(final CommandSender sender) {
        // Reloading all defined languages.
        translator.loadLanguages(plugin.getDataFolder().getAbsolutePath());
        // Reloading plugin configuration.
        plugin.getFancyNpcConfig().reload();
        // Getting the selected language from configuration. Defaults to fallback language.
        final Language selectedLanguage = translator.getLanguages().stream()
                .filter(language -> language.getLanguageName().equals(plugin.getFancyNpcConfig().getLanguage()))
                .findFirst().orElse(translator.getFallbackLanguage());
        translator.setSelectedLanguage(selectedLanguage);
        // Reloading all NPCs.
        // NOTE: This sometimes creates duplicated NPCs on the client-side.
        plugin.getNpcManagerImpl().reloadNpcs();
        // Sending success message to the sender.
        translator.translate("fancynpcs_reload_success").send(sender);
    }

    @Command("fancynpcs save")
    @Permission("fancynpcs.command.fancynpcs.save")
    public void onSave(final CommandSender sender) {
        plugin.getNpcManagerImpl().saveNpcs(true);
        translator.translate("fancynpcs_save_success").send(sender);
    }

    // NOTE: In the future, if there is more than a few feature flags, we might consider listing entries automatically by iterating, just like in 'list' sub-command.
    @Command("fancynpcs feature_flags")
    @Permission("fancynpcs.command.fancynpcs.feature_flags")
    public void onFeatureFlags(final CommandSender sender) {
        translator.translate("fancynpcs_feature_flags_header").send(sender);
        translator.translate("fancynpcs_feature_flags_entry")
                .replace("number", "1")
                .replace("name", "Player NPCs")
                .replace("id", FancyNpcs.PLAYER_NPCS_FEATURE_FLAG.getName())
                .replace("state", getTranslatedState(FancyNpcs.PLAYER_NPCS_FEATURE_FLAG.isEnabled()))
                .send(sender);
        translator.translate("fancynpcs_feature_flags_footer")
                .replace("count", "2")
                .replace("count_formatted", "· · 2")
                .replace("total", String.valueOf(FancyNpcs.getInstance().getNpcManager().getAllNpcs().size()))
                .replace("total_formatted", "· · 2")
                .send(sender);
    }

    // NOTE: Might need to be improved later down the line, should get work done for now.
    private @NotNull String getTranslatedState(final boolean bool) {
        return (bool) ? ((SimpleMessage) translator.translate("enabled")).getMessage() : ((SimpleMessage) translator.translate("disabled")).getMessage();
    }

}
