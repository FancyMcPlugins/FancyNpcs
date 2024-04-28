package de.oliver.fancynpcs.commands.npc;

import de.oliver.fancylib.translations.Translator;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

public enum FixCMD {
    INSTANCE; // SINGLETON

    private final Translator translator = FancyNpcs.getInstance().getTranslator();

    @Command("npc fix")
    @Permission("fancynpcs.command.npc.fix")
    public void onDefault(final CommandSender sender) {
        translator.translate("npc_fix_syntax").send(sender);
    }

    @Command("npc fix <npc>")
    @Permission("fancynpcs.command.npc.fix")
    public void onCommand(final CommandSender sender, final Npc npc) {
        npc.removeForAll();
        npc.create();
        Bukkit.getOnlinePlayers().forEach(npc::checkAndUpdateVisibility);
        translator.translate("npc_fix_success").replace("npc", npc.getData().getName()).send(sender);
    }

}
