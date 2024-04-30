package de.oliver.fancynpcs.commands.npc;

import de.oliver.fancylib.translations.Translator;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Default;
import org.incendo.cloud.annotations.Flag;
import org.incendo.cloud.annotations.Permission;

import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.jetbrains.annotations.Nullable;

public enum ListCMD {
    INSTANCE; // SINGLETON

    private final Translator translator = FancyNpcs.getInstance().getTranslator();

    private static final DecimalFormat COORDS_FORMAT = new DecimalFormat("#.##");

    static {
        COORDS_FORMAT.setMinimumFractionDigits(2);
    }

    @Command("npc list")
    @Permission("fancynpcs.command.npc.list")
    public void onCommand(
            final CommandSender sender,
            final @Nullable @Flag("type") EntityType type,
            final @Flag("sort") @Default("name") SortType sort // NOTE: @Default annotation doesn't work, waiting for a fix.
    ) {
        Stream<Npc> npcs = FancyNpcs.getInstance().getNpcManagerImpl().getAllNpcs().stream();
        // Excluding NPCs not created by the sender, if PLAYER_NPCS_FEATURE_FLAG is enabled and sender is a player.
        if (FancyNpcs.PLAYER_NPCS_FEATURE_FLAG.isEnabled() && sender instanceof Player player)
            npcs = npcs.filter(npc -> npc.getData().getCreator().equals(player.getUniqueId()));
        // Excluding NPCs that are not of a specified type, if desired.
        if (type != null)
            npcs = npcs.filter(npc -> npc.getData().getType() == type);
        // Sorting...
        switch (sort) {
            case NAME -> npcs = npcs.sorted(Comparator.comparing(npc -> npc.getData().getName()));
            case NAME_REVERSED -> npcs = npcs.sorted(Comparator.comparing(npc -> ((Npc) npc).getData().getName()).reversed()); // This needs a cast for some reason.
        }
        translator.translate("npc_list_header").send(sender);
        final AtomicInteger count = new AtomicInteger(0);
        // ...
        npcs.toList().forEach(npc -> {
            final Location location = npc.getData().getLocation();
            // ...
            translator.translate("npc_list_entry")
                    .replace("number", String.valueOf(count.incrementAndGet()))
                    .replace("npc", npc.getData().getName())
                    .replace("location_x", COORDS_FORMAT.format(location.x()))
                    .replace("location_y", COORDS_FORMAT.format(location.y()))
                    .replace("location_z", COORDS_FORMAT.format(location.z()))
                    .replace("world", location.getWorld().getName())
                    .send(sender);
        });
        translator.translate("npc_list_footer")
                .replace("count", String.valueOf(count))
                .replace("total", String.valueOf(FancyNpcs.getInstance().getNpcManager().getAllNpcs().size()))
                .send(sender);
    }

    // SortType enum contains all possible sort types for the '/npc list' command.
    public enum SortType {
        NAME, NAME_REVERSED
    }

}
