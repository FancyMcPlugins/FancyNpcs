package de.oliver.fancynpcs.commands.npc;

import de.oliver.fancylib.translations.Translator;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.events.NpcModifyEvent;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

import org.jetbrains.annotations.NotNull;

public enum InteractionCooldownCMD {
    INSTANCE; // SINGLETON

    private final Translator translator = FancyNpcs.getInstance().getTranslator();

    @Command("npc interaction_cooldown <npc> <cooldown>")
    @Permission("fancynpcs.command.npc.interaction_cooldown")
    public void onInteractionCooldown(
            final @NotNull CommandSender sender,
            final @NotNull Npc npc,
            final float cooldown
    ) {
        // Calling the event and updating the cooldown if not cancelled.
        if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.INTERACTION_COOLDOWN, cooldown, sender).callEvent()) {
            npc.getData().setInteractionCooldown(cooldown);
            translator.translate("npc_interaction_cooldown_success").replace("npc", npc.getData().getName());
        } else {
            translator.translate("command_npc_modification_cancelled").send(sender);
        }
    }

}
