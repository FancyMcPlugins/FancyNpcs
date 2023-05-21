package de.oliver.fancynpcs;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public interface Npc {

    // TODO: fix all the ones that are commented out

    void register();
    void unregister();
    void create();
    void spawn(Player player);
    void spawnForAll();
    void updateDisplayName(String displayName);
//    void updateSkin(SkinFetcher skin);
    void updateGlowing(boolean glowing);
//    void updateGlowingColor(ChatFormatting glowingColor);
    void updateShowInTab(boolean showInTab);
    void lookAt(Player player, Location location);
    void move(Player player, Location location);
    void moveForAll(Location location);
    void remove(Player player);
    void removeForAll();
//    void removeFromTab(Player player); //TODO: private in NpcImpl??
//    void removeFromTabForAll(); //TODO: private in NpcImpl??

    String getName();
//    EntityType<?> getType();
//    void setType(EntityType<?> type);
    String getDisplayName();
    void setDisplayName(String displayName);
//    SkinFetcher getSkin();
//    void setSkin(SkinFetcher skin);
    Location getLocation();
    void setLocation(Location location);
    boolean isShowInTab();
    void setShowInTab(boolean showInTab);
    boolean isSpawnEntity();
    void setSpawnEntity(boolean spawnEntity);
    boolean isGlowing();
    void setGlowing(boolean glowing);
//    ChatFormatting getGlowingColor();
//    void setGlowingColor(ChatFormatting glowingColor);
//    void addEquipment(EquipmentSlot equipmentSlot, ItemStack itemStack); // TODO: nms
//    Map<EquipmentSlot, ItemStack> getEquipment(); // TODO: nms
    Consumer<Player> getOnClick();
    void setOnClick(Consumer<Player> consumer);
    boolean isTurnToPlayer();
    void setTurnToPlayer(boolean turnToPlayer);
    String getMessage();
    void setMessage(String message);
    String getServerCommand();
    void setServerCommand(String serverCommand);
    String getPlayerCommand();
    void setPlayerCommand(String playerCommand);
//    Entity getNpc();
    boolean isDirty();
    void setDirty(boolean dirty);
    boolean isSaveToFile();
    void setSaveToFile(boolean saveToFile);
    Map<UUID, Boolean> getIsTeamCreated();
    Map<UUID, Boolean> getIsVisibleForPlayer();
}
