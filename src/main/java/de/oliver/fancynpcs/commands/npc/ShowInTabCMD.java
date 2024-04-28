package de.oliver.fancynpcs.commands.npc;

import de.oliver.fancylib.translations.Translator;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.events.NpcModifyEvent;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

import org.jetbrains.annotations.Nullable;

public enum ShowInTabCMD {
    INSTANCE; // SINGLETON

    private final Translator translator = FancyNpcs.getInstance().getTranslator();

    @Command("npc show_in_tab")
    @Permission("fancynpcs.command.npc.show_in_tab")
    public void onDefault(final CommandSender sender) {
        translator.translate("npc_show_in_tab_syntax").send(sender);
    }

    @Command("npc show_in_tab <npc> [state]")
    @Permission("fancynpcs.command.npc.show_in_tab")
    public void onCommand(final CommandSender sender, final Npc npc, final @Nullable Boolean state) {
        final boolean finalState = (state == null) ? !npc.getData().isShowInTab() : state;
        // Calling the event and updating the state if not cancelled.
        if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.SHOW_IN_TAB, finalState, sender).callEvent()) {
            // Updating the state.
            npc.getData().setShowInTab(finalState);
            // Sending message to the sender.
            translator.translate(finalState ? "npc_show_in_tab_set_true" : "npc_show_in_tab_set_false").replace("npc", npc.getData().getName()).send(sender);
            // Returning from the command block.
            return;
        }
        // Otherwise, sending error message to the sender.
        translator.translate("command_npc_modification_cancelled").send(sender);
    }

}
