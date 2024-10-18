package de.oliver.fancynpcs.commands.npc;

import de.oliver.fancylib.translations.Translator;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcData;
import de.oliver.fancynpcs.api.events.NpcCreateEvent;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import org.jetbrains.annotations.NotNull;

// TO-DO: Console support with --position and --world parameter flags.
public enum CopyCMD {
    INSTANCE; // SINGLETON

    private static final Pattern NPC_NAME_PATTERN = Pattern.compile("^[A-Za-z0-9/_-]*$");
    private final Translator translator = FancyNpcs.getInstance().getTranslator();

    @Command(value = "npc copy <npc> <name>", requiredSender = Player.class)
    @Permission("fancynpcs.command.npc.copy")
    public void onCopy(
            final @NotNull Player sender,
            final @NotNull Npc npc,
            final @NotNull String name
    ) {
        // Sending error message if name does not match configured pattern.
        if (!NPC_NAME_PATTERN.matcher(name).find()) {
            translator.translate("npc_create_failure_invalid_name").replaceStripped("name", name).send(sender);
            return;
        }
        // Creating a copy of an NPC and all it's data. The only different thing is it's UUID.
        final Npc copied = FancyNpcs.getInstance().getNpcAdapter().apply(
                new NpcData(
                        UUID.randomUUID().toString(),
                        name,
                        sender.getUniqueId(),
                        npc.getData().getDisplayName(),
                        npc.getData().getSkin(),
                        sender.getLocation().clone(),
                        npc.getData().isShowInTab(),
                        npc.getData().isSpawnEntity(),
                        npc.getData().isCollidable(),
                        npc.getData().isGlowing(),
                        npc.getData().getGlowingColor(),
                        npc.getData().getType(),
                        new ConcurrentHashMap<>(npc.getData().getEquipment()),
                        npc.getData().isTurnToPlayer(),
                        npc.getData().getOnClick(),
                        new ConcurrentHashMap<>(npc.getData().getActions()),
                        npc.getData().getInteractionCooldown(),
                        npc.getData().getScale(),
                        new ConcurrentHashMap<>(npc.getData().getAttributes()),
                        npc.getData().isMirrorSkin(),
                        npc.getData().isOnlyVisibleToEnabled(),
                        npc.getData().getOnlyVisibleToPlayers()
                ));
        // Calling the event and creating + registering copied NPC if not cancelled.
        if (new NpcCreateEvent(copied, sender).callEvent()) {
            copied.create();
            FancyNpcs.getInstance().getNpcManagerImpl().registerNpc(copied);
            copied.spawnForAll();
            translator.translate("npc_copy_success").replace("npc", npc.getData().getName()).replace("new_npc", copied.getData().getName()).send(sender);
        } else {
            translator.translate("command_npc_modification_cancelled").send(sender);
        }
    }
}
