package de.oliver.fancynpcs.commands.npc;

import de.oliver.fancylib.translations.Translator;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcData;
import de.oliver.fancynpcs.api.events.NpcCreateEvent;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Flag;
import org.incendo.cloud.annotations.Permission;

import java.util.UUID;
import java.util.regex.Pattern;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum CreateCMD {
    INSTANCE; // SINGLETON

    private final Translator translator = FancyNpcs.getInstance().getTranslator();

    private static final Pattern NPC_NAME_PATTERN = Pattern.compile("^[A-Za-z0-9/_-]*$");
    private static final UUID EMPTY_UUID = new UUID(0,0);

    @Command("npc create <name>")
    @Permission("fancynpcs.command.npc.create")
    public void onCreate(
            final @NotNull CommandSender sender,
            final @NotNull String name,
            final @Nullable @Flag("type") EntityType type,
            final @Nullable @Flag(value = "location", suggestions = "relative_location") Location location,
            final @Nullable @Flag("world") World world
    ) {
        // Sending error message if name does not match configured pattern.
        if (!NPC_NAME_PATTERN.matcher(name).find()) {
            translator.translate("npc_create_failure_invalid_name").replaceStripped("name", name).send(sender);
            return;
        }
        // Getting the NPC creator unique identifier. The UUID is always empty (all zeroes) for non-player senders.
        final UUID creator = (sender instanceof Player player) ? player.getUniqueId() : EMPTY_UUID;
        // Sending error message if NPC with such name already exist.
        if ((FancyNpcs.PLAYER_NPCS_FEATURE_FLAG.isEnabled() && FancyNpcs.getInstance().getNpcManager().getNpc(name, creator) != null) || FancyNpcs.getInstance().getNpcManager().getNpc(name) != null) {
            translator.translate("npc_create_failure_already_exists").replace("npc", FancyNpcs.getInstance().getNpcManager().getNpc(name).getData().getName()).send(sender);
            return;
        }
        // Sending error message if sender is console and location has not been specified.
        if (sender instanceof ConsoleCommandSender && location == null) {
            translator.translate("npc_create_failure_must_specify_location").send(sender);
            return;
        }
        // Sending error message if sender is console and world has not been specified.
        if (sender instanceof ConsoleCommandSender && world == null) {
            translator.translate("npc_create_failure_must_specify_world").send(sender);
            return;
        }
        // Finalizing Location argument. This argument is optional and defaults to player's current location.
        final Location finalLocation = (location == null && sender instanceof Player player) ? player.getLocation() : location;
        // Updating World of the Location argument if '--world' flag has been specified.
        if (world != null)
            finalLocation.setWorld(world);
        // Creating new NPC and applying data.
        final Npc npc = FancyNpcs.getInstance().getNpcAdapter().apply(new NpcData(name, creator, finalLocation));
        // Setting the type of NPC. Flag '--type' is optional and defaults to EntityType.PLAYER.
        npc.getData().setType(type != null ? type : EntityType.PLAYER);
        // Calling the event and creating NPC if not cancelled.
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
