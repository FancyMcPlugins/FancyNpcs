package de.oliver.fancynpcs.converters;

import de.oliver.fancynpcs.converters.citizens.CitizensConverter;

import java.util.HashMap;
import java.util.Map;

public class ConverterManager {

    private final Map<String, Converter> converters;

    public ConverterManager() {
        this.converters = new HashMap<>();

        registerDefaultConverters();
    }

    private void registerDefaultConverters() {
        registerConverter(new CitizensConverter());
    }

    public void registerConverter(Converter converter) {
        converters.put(converter.getPluginName(), converter);
    }

    public Converter getConverter(String pluginName) {
        return converters.getOrDefault(pluginName, null);
    }
}
