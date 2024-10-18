package de.oliver.fancynpcs.listeners;

import com.destroystokyo.paper.profile.ProfileProperty;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.utils.SkinFetcher;
import de.oliver.fancynpcs.v1_19_4.PacketReader_1_19_4;
import de.oliver.fancynpcs.v1_20.PacketReader_1_20;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String mcVersion = Bukkit.getMinecraftVersion();
        if (mcVersion.equals("1.19.4")) {
            PacketReader_1_19_4.inject(event.getPlayer());
        } else if (mcVersion.equals("1.20")) {
            PacketReader_1_20.inject(event.getPlayer());
        }

        for (Npc npc : FancyNpcs.getInstance().getNpcManagerImpl().getAllNpcs()) {
            npc.getIsVisibleForPlayer().put(event.getPlayer().getUniqueId(), false);
            npc.getIsLookingAtPlayer().put(event.getPlayer().getUniqueId(), false);
            npc.getIsTeamCreated().put(event.getPlayer().getUniqueId(), false);
        }

        // don't spawn the npc for player if he just joined
        FancyNpcs.getInstance().getVisibilityTracker().addJoinDelayPlayer(event.getPlayer().getUniqueId());
        FancyNpcs.getInstance().getScheduler().runTaskLater(null, 20L * 2, () -> FancyNpcs.getInstance().getVisibilityTracker().removeJoinDelayPlayer(event.getPlayer().getUniqueId()));

        if (!FancyNpcs.getInstance().getFancyNpcConfig().isMuteVersionNotification() && event.getPlayer().hasPermission("FancyNpcs.admin")) {
            FancyNpcs.getInstance().getScheduler().runTaskAsynchronously(
                    () -> FancyNpcs.getInstance().getVersionConfig().checkVersionAndDisplay(event.getPlayer(), true)
            );
        }

        for (ProfileProperty property : event.getPlayer().getPlayerProfile().getProperties()) {
            if (property.getName().equals("textures")) {
                SkinFetcher.SkinData skinData = new SkinFetcher.SkinData(
                        event.getPlayer().getUniqueId().toString(),
                        property.getValue(),
                        property.getSignature()
                );

                // update the skin cache if the skin is already cached
                if (SkinFetcher.skinCache.containsKey(skinData.identifier())) {
                    SkinFetcher.skinCache.put(skinData.identifier(), skinData);
                }

                // update the skin cache if the skin is already cached
                FancyNpcs.getInstance().getSkinCache().upsert(new SkinFetcher.SkinCacheData(
                        skinData,
                        System.currentTimeMillis(),
                        1000 * 60 * 60 * 24 * 12
                ), true);
            }
        }
    }
}
