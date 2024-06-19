package de.oliver.fancynpcs.commands.npc;

import de.oliver.fancylib.translations.Translator;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.events.NpcModifyEvent;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum CollidableCMD {
    INSTANCE; // SINGLETON

    private final Translator translator = FancyNpcs.getInstance().getTranslator();

    @Command("npc collidable <npc> [state]")
    @Permission("fancynpcs.command.npc.collidable")
    public void onCollidable(
            final @NotNull CommandSender sender,
            final @NotNull Npc npc,
            final @Nullable Boolean state
    ) {
        // Finalizing the state. If no state has been specified, the current one is inverted.
        final boolean finalState = (state == null) ? !npc.getData().isCollidable() : state;
        // Calling the event and updating the state if not cancelled.
        if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.COLLIDABLE, finalState, sender).callEvent()) {
            npc.getData().setCollidable(finalState);
            translator.translate(finalState ? "npc_collidable_set_true" : "npc_collidable_set_false").replace("npc", npc.getData().getName()).send(sender);
            return;
        }
        translator.translate("command_npc_modification_cancelled").send(sender);
    }

}
