package de.oliver.fancynpcs.commands.npc;

import de.oliver.fancylib.translations.Translator;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Flag;
import org.incendo.cloud.annotations.Permission;

import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.jetbrains.annotations.Nullable;

public enum NearbyCMD {
    INSTANCE; // SINGLETON

    private final Translator translator = FancyNpcs.getInstance().getTranslator();

    private static final DecimalFormat COORDS_FORMAT = new DecimalFormat("#.##");
    private static final DecimalFormat DISTANCE_FORMAT = new DecimalFormat("#.#");

    static {
        COORDS_FORMAT.setMinimumFractionDigits(2);
    }

    @Command(value = "npc nearby", requiredSender = Player.class)
    @Permission("fancynpcs.command.npc.nearby")
    public void onCommand(
            final Player sender,
            final @Nullable @Flag("radius") Long radius,
            final @Nullable @Flag("type") EntityType type,
            final @Nullable @Flag("sort") SortType sort // NOTE: Replace with @Default once fixed.
    ) {
        Stream<Npc> npcs = FancyNpcs.getInstance().getNpcManagerImpl().getAllNpcs().stream();
        // Getting location of the sender.
        final Location location = sender.getLocation();
        // Creating a counter which is increased by 1 for every NPC present in player's world.
        final AtomicInteger totalCount = new AtomicInteger(0);
        // Excluding NPCs from different worlds. This also increments the counter defined above.
        npcs = npcs.filter(npc -> {
            if (npc.getData().getLocation().getWorld().equals(location.getWorld())) {
                totalCount.incrementAndGet();
                return true;
            }
            return false;
        });
        // Excluding NPCs not created by the sender, if PLAYER_NPCS_FEATURE_FLAG is enabled and sender is a player.
        if (FancyNpcs.PLAYER_NPCS_FEATURE_FLAG.isEnabled())
            npcs = npcs.filter(npc -> npc.getData().getCreator().equals(sender.getUniqueId()));
        // Excluding NPCs that are not in radius, if specified and sender is a player. (radius is calculated from the location of player)
        if (radius != null)
            npcs = npcs.filter(npc -> npc.getData().getLocation().distance(location) <= radius);
        // Excluding NPCs that are not of a specified type, if desired.
        if (type != null)
            npcs = npcs.filter(npc -> npc.getData().getType() == type);
        // Sorting...
        switch (sort != null ? sort : SortType.NEAREST) { // This should never produce NPE.
            case NAME -> npcs = npcs.sorted(Comparator.comparing(npc -> npc.getData().getName()));
            case NAME_REVERSED -> npcs = npcs.sorted(Comparator.comparing(npc -> ((Npc) npc).getData().getName()).reversed());
            case NEAREST -> npcs = npcs.sorted(Comparator.comparingDouble(npc -> npc.getData().getLocation().distance(location)));
            case FARTHEST -> npcs = npcs.sorted(Comparator.comparingDouble(npc -> ((Npc) npc).getData().getLocation().distance(location)).reversed());
        }
        // Printing the header.
        translator.translate("npc_nearby_header").send(sender);
        // Creating a counter which is increased by 1 for every NPC that is "selected" in this query.
        final AtomicInteger count = new AtomicInteger(0);
        // Printing each entry.
        npcs.toList().forEach(npc -> {
            translator.translate("npc_nearby_entry")
                    .replace("number", String.valueOf(count.incrementAndGet()))
                    .replace("npc", npc.getData().getName())
                    .replace("distance", DISTANCE_FORMAT.format(npc.getData().getLocation().distance(location)))
                    .replace("location_x", COORDS_FORMAT.format(npc.getData().getLocation().x()))
                    .replace("location_y", COORDS_FORMAT.format(npc.getData().getLocation().y()))
                    .replace("location_z", COORDS_FORMAT.format(npc.getData().getLocation().z()))
                    .send(sender);
        });
        // Printing the footer.
        translator.translate("npc_nearby_footer")
                .replace("count", String.valueOf(count))
                .replace("total", String.valueOf(totalCount))
                .send(sender);
    }

    // SortType enum contains all possible sort types for the '/npc nearby' command.
    public enum SortType {
        NAME, NAME_REVERSED, NEAREST, FARTHEST
    }

}
