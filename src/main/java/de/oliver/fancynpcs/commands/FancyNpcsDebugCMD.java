package de.oliver.fancynpcs.commands;

import de.oliver.fancylib.translations.Translator;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.skins.mineskin.MineSkinQueue;
import de.oliver.fancynpcs.skins.mojang.MojangQueue;
import de.oliver.fancynpcs.tests.FancyNpcsTests;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public final class FancyNpcsDebugCMD {

    public static final FancyNpcsDebugCMD INSTANCE = new FancyNpcsDebugCMD();
    private final Translator translator = FancyNpcs.getInstance().getTranslator();
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private FancyNpcsDebugCMD() {
    }

    @Command("fancynpcs run_tests")
    @Permission("fancynpcs.command.fancynpcs.run_tests")
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

    @Command("fancynpcs skin_system restart_schedulers")
    @Permission("fancynpcs.command.fancynpcs.skin_system.restart_schedulers")
    public void onSkinSchedulerRestart(final Player player) {
        MineSkinQueue.get().getScheduler().cancel(true);
        MojangQueue.get().getScheduler().cancel(true);

        MineSkinQueue.get().run();
        MojangQueue.get().run();

        translator.translate("fancynpcs_skin_system_restart_schedulers_success").send(player);
    }

    @Command("fancynpcs skin_system clear_queues")
    @Permission("fancynpcs.command.fancynpcs.skin_system.clear_queues")
    public void onClearSkinQueues(final Player player) {
        MineSkinQueue.get().clear();
        MojangQueue.get().clear();

        translator.translate("fancynpcs_skin_system_clear_queues_success").send(player);
    }

    @Command("fancynpcs skin_system clear_cache")
    @Permission("fancynpcs.command.fancynpcs.skin_system.clear_cache")
    public void onInvalidateCache(final Player player) {
        FancyNpcs.getInstance().getSkinManagerImpl().getMemCache().clear();
        FancyNpcs.getInstance().getSkinManagerImpl().getFileCache().clear();

        translator.translate("fancynpcs_skin_system_clear_cache_success").send(player);
    }

}
