package de.oliver.fancynpcs.commands.npc;

import de.oliver.fancylib.translations.Translator;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcData;
import de.oliver.fancynpcs.api.events.NpcCreateEvent;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Flag;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotations.Regex;

import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum CreateCMD {
    INSTANCE; // SINGLETON

    private final Translator translator = FancyNpcs.getInstance().getTranslator();

    @Command("npc create <name>")
    @Permission("fancynpcs.command.npc.create")
    public void onCreateCommand(final @NotNull CommandSender sender,
                                final @NotNull @Regex("^[A-Za-z0-9_-]*$") String name,
                                final @Nullable @Flag("type") EntityType type,
                                final @Nullable @Flag("position") Location position,
                                final @Nullable @Flag("world") World world
    ) {
        if (FancyNpcs.getInstance().getNpcManager().getNpc(name) != null) {
            translator.translate("npc_create_failure_already_exists").replace("npc", FancyNpcs.getInstance().getNpcManager().getNpc(name).getData().getName()).send(sender);
            return;
        }
        final EntityType finalType = (type != null) ? type : EntityType.PLAYER;
        // Getting the Location where NPC will be created at.
        final Location location = (position == null && sender instanceof Player player) ? player.getLocation() : position;
        // Setting the world of specified location.
        if (location.getWorld() == null) {
            if (world == null) {
                translator.translate("npc_create_failure_must_specify_world").send(sender);
                return;
            }
            location.setWorld(world);
        }
        final Npc npc = FancyNpcs.getInstance().getNpcAdapter().apply(new NpcData(name, (sender instanceof Player player) ? player.getUniqueId() : UUID.nameUUIDFromBytes(new byte[0]), location));
        // Setting the type of NPC. Default type is EntityType.PLAYER.
        npc.getData().setType(finalType);
        // Calling event and creating NPC if not cancelled, sending error message otherwise.
        if (new NpcCreateEvent(npc, sender).callEvent()) {
            npc.create();
            FancyNpcs.getInstance().getNpcManagerImpl().registerNpc(npc);
            npc.spawnForAll();
            translator.translate("npc_create_success").replace("npc", name).send(sender);
        } else {
            translator.translate("command_npc_modification_cancelled").send(sender);
        }
    }

}
