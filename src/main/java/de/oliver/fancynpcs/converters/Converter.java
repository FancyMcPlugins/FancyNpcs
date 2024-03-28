package de.oliver.fancynpcs.converters;

import de.oliver.fancynpcs.api.Npc;

import java.util.List;

public abstract class Converter {

    private final String pluginName;

    public Converter(String pluginName) {
        this.pluginName = pluginName;
    }

    public abstract List<Npc> convertAll();

    public abstract Npc convertOne(String npcName);

    public String getPluginName() {
        return pluginName;
    }
}
