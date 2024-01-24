package de.oliver.fancynpcs.commands.npc;

import de.oliver.fancylib.LanguageConfig;
import de.oliver.fancylib.MessageHelper;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.commands.Subcommand;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class InfoCMD implements Subcommand {

    private final LanguageConfig lang = FancyNpcs.getInstance().getLanguageConfig();

    @Override
    public List<String> tabcompletion(@NotNull Player player, @Nullable Npc npc, @NotNull String[] args) {
        return null;
    }

    @Override
    public boolean run(@NotNull CommandSender receiver, @Nullable Npc npc, @NotNull String[] args) {
        if (npc == null) {
            MessageHelper.error(receiver, lang.get("npc-not-found"));
            return false;
        }

        Location loc = npc.getData().getLocation();

        MessageHelper.info(receiver, "<b>NPC: " + npc.getData().getName());
        MessageHelper.info(receiver, " - Id: <gray>" + npc.getData().getId());
        MessageHelper.info(receiver, " - Creator: <gray>" + npc.getData().getCreator());
        MessageHelper.info(receiver, " - Display name: <gray>" + npc.getData().getDisplayName());
        MessageHelper.info(receiver, " - Location: <gray>" + loc.getWorld().getName() + " " + loc.getBlockX() + "/" + loc.getBlockY() + "/" + loc.getBlockZ());
        MessageHelper.info(receiver, " - Type: <gray>" + npc.getData().getType().name());
        MessageHelper.info(receiver, " - Show in tab: <gray>" + npc.getData().isShowInTab());
        MessageHelper.info(receiver, " - Turn to player: <gray>" + npc.getData().isTurnToPlayer());
        MessageHelper.info(receiver, " - Is glowing: <gray>" + npc.getData().isGlowing());
        MessageHelper.info(receiver, " - Glowing color: <gray>" + npc.getData().getGlowingColor().toString());
        MessageHelper.info(receiver, " - Is collidable: <gray>" + npc.getData().isCollidable());
        MessageHelper.info(receiver, " - Interaction cooldown: <gray>" + npc.getData().getInteractionCooldown() + " seconds");
        MessageHelper.info(receiver, " - Server Command: <gray>" + npc.getData().getServerCommand());

        if (!npc.getData().getPlayerCommands().isEmpty()) {
            MessageHelper.info(receiver, " - Player commands:");
            for (int i = 0; i < npc.getData().getMessages().size(); i++) {
                MessageHelper.info(receiver, " <gray>" + (i + 1) + ": " + npc.getData().getPlayerCommands().get(i));
            }
        }

        if (!npc.getData().getMessages().isEmpty()) {
            MessageHelper.info(receiver, " - Messages:");
            for (int i = 0; i < npc.getData().getMessages().size(); i++) {
                MessageHelper.info(receiver, " <gray>" + (i + 1) + ": " + npc.getData().getMessages().get(i));
            }
        }


        npc.getData().getEquipment().forEach((slot, item) ->
                MessageHelper.info(receiver, " - Equipment: <gray>" + slot.name() + " -> " + item.getType().name())
        );

        npc.getData().getAttributes().forEach((attribute, value) ->
                MessageHelper.info(receiver, " - Attribute: <gray>" + attribute.getName() + " -> " + value)
        );

        return false;
    }
}
