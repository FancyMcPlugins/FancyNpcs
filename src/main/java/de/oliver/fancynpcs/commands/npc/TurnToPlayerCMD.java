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
        if (state != null && npc.getData().isTurnToPlayer() != state) {
            if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.TURN_TO_PLAYER, state, sender).callEvent()) {
                npc.getData().setTurnToPlayer(state);
                translator.translate(state ? "npc_turn_to_player_set_true" : "npc_turn_to_player_set_false")
                        .replace("npc", npc.getData().getName())
                        .send(sender);
            } else {
                translator.translate("command_npc_modification_cancelled").send(sender);
            }
        } else if (state == null) {
            // If no state provided, just display current state
            boolean currentState = npc.getData().isTurnToPlayer();
            translator.translate(currentState ? "npc_turn_to_player_status_true" : "npc_turn_to_player_status_false")
                    .replace("npc", npc.getData().getName())
                    .send(sender);
        }
    }
}
