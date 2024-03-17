package de.oliver.fancynpcs.commands.npc;

import de.oliver.fancylib.LanguageConfig;
import de.oliver.fancylib.MessageHelper;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.events.NpcModifyEvent;
import de.oliver.fancynpcs.commands.Subcommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Stream;

public class TurnToPlayerCMD implements Subcommand {

    private final LanguageConfig lang = FancyNpcs.getInstance().getLanguageConfig();

    @Override
    public List<String> tabcompletion(@NotNull Player player, @Nullable Npc npc, @NotNull String[] args) {
        if (args.length == 3) {
            return Stream.of("true", "false")
                    .filter(input -> input.toLowerCase().startsWith(args[2].toLowerCase()))
                    .toList();
        }
        
        return null;
    }

    @Override
    public boolean run(@NotNull CommandSender receiver, @Nullable Npc npc, @NotNull String[] args) {
        if (args.length < 3) {
            MessageHelper.error(receiver, lang.get("wrong-usage"));
            return false;
        }


        if (npc == null) {
            MessageHelper.error(receiver, lang.get("npc-not-found"));
            return false;
        }

        boolean turnToPlayer;
        try {
            turnToPlayer = Boolean.parseBoolean(args[2]);
        } catch (Exception e) {
            MessageHelper.error(receiver, lang.get("wrong-usage"));
            return false;
        }

        NpcModifyEvent npcModifyEvent = new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.TURN_TO_PLAYER, turnToPlayer, receiver);
        npcModifyEvent.callEvent();

        if (!npcModifyEvent.isCancelled()) {
            npc.getData().setTurnToPlayer(turnToPlayer);

            if (turnToPlayer) {
                MessageHelper.success(receiver, lang.get("npc-command-turnToPlayer-true"));
            } else {
                MessageHelper.success(receiver, lang.get("npc-command-turnToPlayer-false"));
                npc.updateForAll(); // move to default pos
            }
        } else {
            MessageHelper.error(receiver, lang.get("npc-command-modification-cancelled"));
        }

        return true;
    }
}
