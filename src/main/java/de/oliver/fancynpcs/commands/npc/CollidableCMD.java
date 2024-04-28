package de.oliver.fancynpcs.commands.npc;

import de.oliver.fancylib.translations.Translator;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.events.NpcModifyEvent;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

import org.jetbrains.annotations.Nullable;

public enum CollidableCMD {
    INSTANCE; // SINGLETON

    private final Translator translator = FancyNpcs.getInstance().getTranslator();

    @Command("npc collidable")
    @Permission("fancynpcs.command.npc.collidable")
    public void onDefault(final CommandSender sender) {
        translator.translate("npc_collidable_syntax").send(sender);
    }

    @Command("npc collidable <npc> [state]")
    @Permission("fancynpcs.command.npc.collidable")
    public void onCommand(final CommandSender sender, final Npc npc, final @Nullable Boolean state) {
        final boolean finalState = (state == null) ? !npc.getData().isCollidable() : state;
        // Calling the event and updating the state if not cancelled.
        if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.COLLIDABLE, finalState, sender).callEvent()) {
            // Updating the state.
            npc.getData().setCollidable(finalState);
            // Sending message to the sender.
            translator.translate(finalState ? "npc_collidable_set_true" : "npc_collidable_set_false").replace("npc", npc.getData().getName()).send(sender);
            // Returning from the command block.
            return;
        }
        // Otherwise, sending error message to the sender.
        translator.translate("command_npc_modification_cancelled").send(sender);
    }

}
