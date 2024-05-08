package de.oliver.fancynpcs.commands.npc;

import de.oliver.fancylib.translations.Translator;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcData;
import de.oliver.fancynpcs.api.events.NpcCreateEvent;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotations.Regex;

import java.util.UUID;

// TO-DO: Console support with --position and --world parameter flags.
public enum CopyCMD {
    INSTANCE; // SINGLETON

    private final Translator translator = FancyNpcs.getInstance().getTranslator();

    @Command(value = "npc copy", requiredSender = Player.class)
    @Permission("fancynpcs.command.npc.copy")
    public void onDefault(final Player sender) {
        translator.translate("npc_copy_syntax").send(sender);
    }

    @Command(value = "npc copy <npc> <name>", requiredSender = Player.class)
    @Permission("fancynpcs.command.npc.copy")
    public void onCommand(final Player sender, final Npc npc, final @Regex("^[A-Za-z0-9_-]*$") String name) {
        // Creating a copy of an NPC and all it's data. The only different thing is it's UUID.
        final Npc copied = FancyNpcs.getInstance().getNpcAdapter().apply(
                new NpcData(
                        UUID.randomUUID().toString(),
                        name,
                        sender.getUniqueId(),
                        npc.getData().getDisplayName(),
                        npc.getData().getSkin(),
                        sender.getLocation(),
                        npc.getData().isShowInTab(),
                        npc.getData().isSpawnEntity(),
                        npc.getData().isCollidable(),
                        npc.getData().isGlowing(),
                        npc.getData().getGlowingColor(),
                        npc.getData().getType(),
                        npc.getData().getEquipment(),
                        npc.getData().isTurnToPlayer(),
                        npc.getData().getOnClick(),
                        npc.getData().getMessages(),
                        npc.getData().isSendMessagesRandomly(),
                        npc.getData().getServerCommand(),
                        npc.getData().getPlayerCommands(),
                        npc.getData().getInteractionCooldown(),
                        npc.getData().getAttributes(),
                        npc.getData().isMirrorSkin()
                ));
        // Calling the event and creating + registering copied NPC if not cancelled.
        if (new NpcCreateEvent(copied, sender).callEvent()) {
            copied.create();
            FancyNpcs.getInstance().getNpcManagerImpl().registerNpc(copied);
            copied.spawnForAll();
            translator.translate("npc_command_copy_success").replace("npc", npc.getData().getName()).replace("new_npc", copied.getData().getName()).send(sender);
        } else {
            translator.translate("command_npc_modification_cancelled").send(sender);
        }
    }
}
