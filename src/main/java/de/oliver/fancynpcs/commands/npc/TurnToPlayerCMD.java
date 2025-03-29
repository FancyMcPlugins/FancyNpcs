package de.oliver.fancynpcs.commands.npc;

import de.oliver.fancylib.translations.Translator;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.events.NpcModifyEvent;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.jetbrains.annotations.NotNull;

public enum TurnToPlayerCMD {
    INSTANCE; // SINGLETON

    private final Translator translator = FancyNpcs.getInstance().getTranslator();

    @Command("npc turn_to_player <npc> <state> [distance]")
    @Permission("fancynpcs.command.npc.turn_to_player")
    public void onTurnToPlayer(
            final @NotNull CommandSender sender,
            final @NotNull Npc npc,
            final boolean state,
            final Integer distance
    ) {
        // Handle turn to player state
        if (npc.getData().isTurnToPlayer() != state) {
            if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.TURN_TO_PLAYER, state, sender).callEvent()) {
                npc.getData().setTurnToPlayer(state);
                translator.translate(state ? "npc_turn_to_player_set_true" : "npc_turn_to_player_set_false")
                        .replace("npc", npc.getData().getName())
                        .send(sender);
            } else {
                translator.translate("command_npc_modification_cancelled").send(sender);
                return;
            }
        }
        
        // Handle distance parameter if provided
        if (distance != null) {
            if (distance < -1) {
                translator.translate("npc_turn_to_player_distance_invalid").send(sender);
                return;
            }
            
            if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.TURN_TO_PLAYER_DISTANCE, distance, sender).callEvent()) {
                npc.getData().setTurnToPlayerDistance(distance);
                
                if (distance == -1) {
                    // Using default distance
                    int defaultDistance = FancyNpcs.getInstance().getFancyNpcConfig().getTurnToPlayerDistance();
                    translator.translate("npc_turn_to_player_distance_default")
                            .replace("npc", npc.getData().getName())
                            .replace("distance", String.valueOf(defaultDistance))
                            .send(sender);
                } else {
                    // Using custom distance
                    translator.translate("npc_turn_to_player_distance_set")
                            .replace("npc", npc.getData().getName())
                            .replace("distance", String.valueOf(distance))
                            .send(sender);
                }
            } else {
                translator.translate("command_npc_modification_cancelled").send(sender);
            }
        }
    }
}
