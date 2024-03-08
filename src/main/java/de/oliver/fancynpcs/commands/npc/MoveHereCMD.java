package de.oliver.fancynpcs.commands.npc;

import de.oliver.fancylib.LanguageConfig;
import de.oliver.fancylib.MessageHelper;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.events.NpcModifyEvent;
import de.oliver.fancynpcs.commands.Subcommand;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MoveHereCMD implements Subcommand {

    private final LanguageConfig lang = FancyNpcs.getInstance().getLanguageConfig();

    @Override
    public List<String> tabcompletion(@NotNull Player player, @Nullable Npc npc, @NotNull String[] args) {
        return null;
    }

    @Override
    public boolean run(@NotNull CommandSender receiver, @Nullable Npc npc, @NotNull String[] args) {
        if (!(receiver instanceof Player player)) {
            MessageHelper.error(receiver, lang.get("npc-command.only-players"));
            return false;
        }

        if (npc == null) {
            MessageHelper.error(receiver, lang.get("npc-not-found"));
            return false;
        }

        Location location = player.getLocation();

        NpcModifyEvent npcModifyEvent = new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.LOCATION, location, receiver);
        npcModifyEvent.callEvent();

        String oldWorld = npc.getData().getLocation().getWorld().getName();

        if (!npcModifyEvent.isCancelled()) {
            npc.getData().setLocation(location);

            if (oldWorld.equals(location.getWorld().getName())) {
                npc.updateForAll();
            } else {
                npc.removeForAll();
                npc.spawnForAll();
            }

            MessageHelper.success(receiver, lang.get("npc-command-moveHere-moved", "npc", npc.getData().getName()));
        } else {
            MessageHelper.error(receiver, lang.get("npc-command-modification-cancelled", "npc", npc.getData().getName()));
        }

        return true;
    }
}
