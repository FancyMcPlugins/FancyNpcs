package de.oliver.fancynpcs.commands.npc;

import de.oliver.fancylib.translations.Translator;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.events.NpcModifyEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.jetbrains.annotations.NotNull;

public enum TypeCMD {
    INSTANCE; // SINGLETON

    private final Translator translator = FancyNpcs.getInstance().getTranslator();

    @Command("npc type <npc> <type>")
    @Permission("fancynpcs.command.npc.type")
    public void onType(
            final @NotNull CommandSender sender,
            final @NotNull Npc npc,
            final @NotNull EntityType type
    ) {
        // Calling the event and updating the type if not cancelled.
        if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.TYPE, type, sender).callEvent()) {
            npc.getData().setType(type);

            if (type != EntityType.PLAYER) {
                npc.getData().setShowInTab(false);
                npc.getData().setSkin(null);
                npc.getData().setMirrorSkin(false);
            }

            if (!type.isAlive() && npc.getData().getEquipment() != null) {
                npc.getData().getEquipment().clear();
            }

            FancyNpcsPlugin.get().getNpcThread().submit(() -> {
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    npc.remove(onlinePlayer);
                }
                npc.create();
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    npc.spawn(onlinePlayer);
                }
            });
            translator.translate("npc_type_success").replace("npc", npc.getData().getName()).replace("type", type.name().toLowerCase()).send(sender);
        } else {
            translator.translate("command_npc_modification_cancelled").send(sender);
        }
    }
}
