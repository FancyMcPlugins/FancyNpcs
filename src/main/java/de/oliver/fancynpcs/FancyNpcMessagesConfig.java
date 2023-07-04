package de.oliver.fancynpcs;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FancyNpcMessagesConfig {
    private final Map<String, Object> objectMap = new HashMap<>();

    public void reload() {
        objectMap.clear();
        FileConfiguration config = YamlConfiguration.loadConfiguration(new File(FancyNpcs.getInstance().getDataFolder(), "messages.yml"));
        for (String key : config.getKeys(true)) {
            if (config.isConfigurationSection(key)) continue;
            objectMap.put(key, config.get(key));
        }
    }

    public Object get(String key) {
        return objectMap.get(key);
    }

    public String getString(String key) {
        return (String) get(key);
    }

    public int getInt(String key) {
        return (Integer) get(key);
    }

    public long getLong(String key) {
        return (Long) get(key);
    }

    public short getShort(String key) {
        return (Short) get(key);
    }

    public double getDouble(String key) {
        return (Double) get(key);
    }

    public float getFloat(String key) {
        return (Float) get(key);
    }

    public boolean getBoolean(String key) {
        return (Boolean) get(key);
    }

    public Object getObject(String key) {
        return get(key);
    }

    public List getList(String key) {
        return (List) get(key);
    }

    public List<String> getStringList(String key) {
        return (List<String>) get(key);
    }

    public List<Integer> getIntList(String key) {
        return (List<Integer>) get(key);
    }

    public List<Long> getLongList(String key) {
        return (List<Long>) get(key);
    }

    public List<Short> getShortList(String key) {
        return (List<Short>) get(key);
    }

    public List<Double> getDoubleList(String key) {
        return (List<Double>) get(key);
    }

    public List<Float> getFloatList(String key) {
        return (List<Float>) get(key);
    }

    public List<Boolean> getBooleanList(String key) {
        return (List<Boolean>) get(key);
    }

    public boolean contains(String key) {
        return get(key) != null;
    }
}
