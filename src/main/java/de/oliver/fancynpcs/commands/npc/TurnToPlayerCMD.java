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

public enum TurnToPlayerCMD {
    INSTANCE; // SINGLETON

    private final Translator translator = FancyNpcs.getInstance().getTranslator();

    @Command("npc turn_to_player <npc> [state]")
    @Permission("fancynpcs.command.npc.turn_to_player")
    public void onTurnToPlayer(
            final @NotNull CommandSender sender,
            final @NotNull Npc npc,
            final @Nullable Boolean state
    ) {
        final boolean finalState = (state == null) ? !npc.getData().isTurnToPlayer() : state;
        // Calling the event and updating the state if not cancelled.
        if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.TURN_TO_PLAYER, finalState, sender).callEvent()) {
            npc.getData().setTurnToPlayer(finalState);
            translator.translate(finalState ? "npc_turn_to_player_set_true" : "npc_turn_to_player_set_false").replace("npc", npc.getData().getName()).send(sender);
            return;
        }
        translator.translate("command_npc_modification_cancelled").send(sender);
    }

}
