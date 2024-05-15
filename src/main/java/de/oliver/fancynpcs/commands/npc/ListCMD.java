package de.oliver.fancynpcs.commands.npc;

import de.oliver.fancylib.translations.Translator;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Flag;
import org.incendo.cloud.annotations.Permission;

import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;
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
            final @NotNull CommandSender sender,
            final @Nullable @Flag("type") EntityType type,
            final @Nullable @Flag("sort") SortType sort
    ) {
        Stream<Npc> stream = FancyNpcs.getInstance().getNpcManagerImpl().getAllNpcs().stream();
        // Excluding NPCs not created by the sender, if PLAYER_NPCS_FEATURE_FLAG is enabled and sender is a player.
        if (FancyNpcs.PLAYER_NPCS_FEATURE_FLAG.isEnabled() && sender instanceof Player player)
            stream = stream.filter(npc -> npc.getData().getCreator().equals(player.getUniqueId()));
        // Excluding NPCs that are not of a specified type, if desired.
        if (type != null)
            stream = stream.filter(npc -> npc.getData().getType() == type);
        // Sorting based on SortType choice. Defaults to SortType.NAME. There might be more sort types in the future which should be handled here accordingly.
        switch (sort != null ? sort : SortType.NAME) {
            case NAME -> stream = stream.sorted(Comparator.comparing(npc -> npc.getData().getName()));
            case NAME_REVERSED -> stream = stream.sorted(Comparator.comparing(npc -> ((Npc) npc).getData().getName()).reversed()); // This needs a cast for some reason.
        }
        translator.translate("npc_list_header").send(sender);
        // Using AtomicInteger counter because streams don't expose entry index.
        final AtomicInteger count = new AtomicInteger(0);
        // Iterating over each NPC referenced in the stream. Usage of forEachOrdered should presumably preserve element order.
        stream.forEachOrdered(npc -> {
            translator.translate("npc_list_entry")
                    .replace("number", String.valueOf(count.incrementAndGet()))
                    .replace("npc", npc.getData().getName())
                    .replace("location_x", COORDS_FORMAT.format(npc.getData().getLocation().x()))
                    .replace("location_y", COORDS_FORMAT.format(npc.getData().getLocation().y()))
                    .replace("location_z", COORDS_FORMAT.format(npc.getData().getLocation().z()))
                    .replace("world", npc.getData().getLocation().getWorld().getName())
                    .send(sender);
        });
        final int totalCount = FancyNpcs.getInstance().getNpcManager().getAllNpcs().size();
        translator.translate("npc_list_footer")
                .replace("count", String.valueOf(count))
                .replace("count_formatted", "· ".repeat(3 - String.valueOf(count).length()) + count)
                .replace("total", String.valueOf(FancyNpcs.getInstance().getNpcManager().getAllNpcs().size()))
                .replace("total_formatted", "· ".repeat(3 - String.valueOf(totalCount).length()) + totalCount)
                .send(sender);
    }

    /**
     * {@link SortType ListCMD.SortType} enum contains all possible sort types for the {@code /npc list} command.
     */
    public enum SortType {
        NAME, NAME_REVERSED
    }

}
