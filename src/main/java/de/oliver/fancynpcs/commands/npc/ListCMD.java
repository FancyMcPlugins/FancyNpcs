package de.oliver.fancynpcs.commands.npc;

import de.oliver.fancylib.LanguageConfig;
import de.oliver.fancylib.MessageHelper;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.commands.Subcommand;
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

    @Override
    public boolean run(@NotNull Player player, @Nullable Npc npc, @NotNull String[] args) {
        if (!player.hasPermission("fancynpcs.npc.list") && !player.hasPermission("fancynpcs.npc.*")) {
            MessageHelper.error(player, lang.get("no-permission-subcommand"));
            return false;
        }

        MessageHelper.info(player, lang.get("npc-command-list-header"));

        Collection<Npc> allNpcs = FancyNpcs.getInstance().getNpcManagerImpl().getAllNpcs();
        if (FancyNpcs.PLAYER_NPCS_FEATURE_FLAG.isEnabled()) {
            allNpcs = allNpcs.stream()
                    .filter(n -> n.getData().getCreator().equals(player.getUniqueId()))
                    .toList();
        }

        if (allNpcs.isEmpty()) {
            MessageHelper.warning(player, lang.get("npc-command-list-no-npcs"));
        } else {
            final DecimalFormat df = new DecimalFormat("#########.##");
            for (Npc n : allNpcs) {
                MessageHelper.info(player, lang.get(
                                "npc-command-list-tp-hover",
                                "name", n.getData().getName(),
                                "x", df.format(n.getData().getLocation().x()),
                                "y", df.format(n.getData().getLocation().y()),
                                "z", df.format(n.getData().getLocation().z()),
                                "tp_cmd", "/tp " + n.getData().getLocation().x() + " " + n.getData().getLocation().y() + " " + n.getData().getLocation().z()
                        )
                );
            }
        }

        return true;
    }
}
