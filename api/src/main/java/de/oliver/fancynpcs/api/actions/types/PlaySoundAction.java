package de.oliver.fancynpcs.api.actions.types;

import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.actions.NpcAction;
import de.oliver.fancynpcs.api.actions.executor.ActionExecutionContext;
import org.jetbrains.annotations.NotNull;
import org.lushplugins.chatcolorhandler.ChatColorHandler;
import org.lushplugins.chatcolorhandler.parsers.ParserTypes;

public class PlaySoundAction extends NpcAction {

    public PlaySoundAction() {
        super("play_sound", true);
    }

    @Override
    public void execute(@NotNull ActionExecutionContext context, String value) {
        if (value == null || value.isEmpty()) {
            return;
        }

        if (context.getPlayer() == null) {
            return;
        }

        String sound = ChatColorHandler.translate(value, context.getPlayer(), ParserTypes.placeholder());

        FancyNpcsPlugin.get().getScheduler().runTask(
                context.getPlayer().getLocation(),
                () -> {
                    try {
                        context.getPlayer().playSound(context.getPlayer().getLocation(), value, 1.0F, 1.0F);
                    } catch (Exception e) {
                        FancyNpcsPlugin.get().getFancyLogger().warn("Failed to play sound: " + sound);
                    }
                });
    }
}
