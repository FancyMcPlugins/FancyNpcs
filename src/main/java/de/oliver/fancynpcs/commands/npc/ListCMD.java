package de.oliver.fancynpcs.commands.npc;

import de.oliver.fancylib.LanguageConfig;
import de.oliver.fancylib.MessageHelper;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.commands.Subcommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.List;

public class ListCMD implements Subcommand {

    private final LanguageConfig lang = FancyNpcs.getInstance().getLanguageConfig();

    @Override
    public List<String> tabcompletion(@NotNull Player player, @Nullable Npc npc, @NotNull String[] args) {
        return null;
    }

    @Command("npc list")
    @Permission("fancynpcs.command.npc.list")
    public void onCommand(
            final CommandSender sender,
            final @Nullable @Flag("radius") Long radius,
            final @Nullable @Flag("type") EntityType type,
            final @Nullable @Flag("sort") SortType sort
    ) {
        Stream<Npc> npcs = FancyNpcs.getInstance().getNpcManagerImpl().getAllNpcs().stream();
        // Excluding NPCs not created by the sender, if PLAYER_NPCS_FEATURE_FLAG is enabled and sender is a player.
        if (FancyNpcs.PLAYER_NPCS_FEATURE_FLAG.isEnabled() && sender instanceof Player player)
            npcs = npcs.filter(npc -> npc.getData().getCreator().equals(player.getUniqueId()));
        // Excluding NPCs that are not in radius, if specified and sender is a player. (radius is calculated from the location of player)
        if (radius != null && sender instanceof Player player)
            npcs = npcs.filter(npc -> npc.getData().getLocation().distance(player.getLocation()) <= radius);
        // Excluding NPCs that are not of a specified type, if desired.
        if (type != null)
            npcs = npcs.filter(npc -> npc.getData().getType() == type);
        // Sorting...
        switch (sort != null ? sort : (sender instanceof Player player) ? SortType.NEAREST : SortType.NAME) {
            case NEAREST -> {
                if (sender instanceof Player player)
                    npcs = npcs.sorted(Comparator.comparingDouble(npc -> npc.getData().getLocation().distance(player.getLocation())));
                // ...
                else sender.sendMessage("Cannot use SortType.NEAREST from console."); // TODO
            }
            case FARTHEST -> {
                if (sender instanceof Player player)
                    // This needs a cast for some reason.
                    npcs = npcs.sorted(Comparator.comparingDouble(npc -> ((Npc) npc).getData().getLocation().distance(player.getLocation())).reversed());
                    // ...
                else sender.sendMessage("Cannot use SortType.FARTHEST from console."); // TODO
            }
            case NAME -> {
                npcs = npcs.sorted(Comparator.comparing(npc -> npc.getData().getName()));
            }
            case NAME_REVERSED -> {
                // This needs a cast for some reason.
                npcs = npcs.sorted(Comparator.comparing(npc -> ((Npc) npc).getData().getName()).reversed());
            }
        }
        translator.translate("npc_list_header").send(sender);
        final AtomicInteger count = new AtomicInteger(1);
        // ...
        npcs.toList().forEach(npc -> {
            final Location loc = npc.getData().getLocation();
            // ...
            translator.translate(sender instanceof Player player ? "npc_list_entry_player" : "npc_list_entry")
                    .replace("num", String.valueOf(count.getAndIncrement()))
                    .replace("npc", npc.getData().getName())
                    .replace("name", npc.getData().getName())
                    .replace("id", npc.getData().getId())
                    .replace("id_short", npc.getData().getId().substring(0, 13) + "...")
                    .replace("internal_id", "2")
                    .replace("creator", npc.getData().getCreator().toString())
                    .replace("creator_short", npc.getData().getCreator().toString().substring(0, 13) + "...")
                    .replace("displayname", npc.getData().getDisplayName())
                    .replace("type", npc.getData().getType().toString())
                    .replace("location_x", COORDS_FORMAT.format(loc.x()))
                    .replace("location_y", COORDS_FORMAT.format(loc.y()))
                    .replace("location_z", COORDS_FORMAT.format(loc.z()))
                    .replace("distance", (sender instanceof Player player) ? new DecimalFormat("#.#").format(player.getLocation().distance(npc.getData().getLocation())) : "N/A")
                    .replace("world", loc.getWorld().getName())
                    .send(sender);
        });
        translator.translate("npc_list_footer")
                .replace("count", String.valueOf(count))
                .replace("total", String.valueOf(FancyNpcs.getInstance().getNpcManager().getAllNpcs().size()))
                .send(sender);
    }

    public enum SortType {
        NEAREST, FARTHEST, NAME, NAME_REVERSED
    }

}
