package de.oliver.fancynpcs.commands;

import de.oliver.fancylib.MessageHelper;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.converters.Converter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class NpcConverterCMD extends Command {
    public NpcConverterCMD() {
        super("npcconverter");
        setPermission("fancynpcs.npcconverter");
    }

    @Override
    public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] args) {

        String pluginName = args[0];

        Converter converter = FancyNpcs.getInstance().getConverterManager().getConverter(pluginName);
        if (converter == null) {
            MessageHelper.error(commandSender, "No converter found for plugin " + pluginName);
            return false;
        }

        List<Npc> npcs = converter.convertAll();
        for (Npc npc : npcs) {
            FancyNpcs.getInstance().getNpcManager().registerNpc(npc);
            npc.create();
            npc.spawnForAll();
            MessageHelper.info(commandSender, "Converted npc " + npc.getData().getName() + " from plugin " + pluginName);
        }

        return false;
    }
}
