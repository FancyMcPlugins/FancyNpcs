package de.oliver.fancynpcs.impl;

import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcAttribute;
import de.oliver.fancynpcs.api.NpcData;
import de.oliver.fancynpcs.api.events.NpcSpawnEvent;
import de.oliver.fancysitula.api.entities.FS_Entity;
import de.oliver.fancysitula.api.entities.FS_Player;
import de.oliver.fancysitula.api.entities.FS_RealPlayer;
import de.oliver.fancysitula.api.entities.FS_TextDisplay;
import de.oliver.fancysitula.api.packets.FS_ClientboundPlayerInfoUpdatePacket;
import de.oliver.fancysitula.api.packets.FS_Color;
import de.oliver.fancysitula.api.teams.FS_CollisionRule;
import de.oliver.fancysitula.api.teams.FS_NameTagVisibility;
import de.oliver.fancysitula.api.teams.FS_Team;
import de.oliver.fancysitula.api.utils.FS_EquipmentSlot;
import de.oliver.fancysitula.api.utils.FS_GameProfile;
import de.oliver.fancysitula.api.utils.FS_GameType;
import de.oliver.fancysitula.factories.FancySitula;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.lushplugins.chatcolorhandler.ModernChatColorHandler;

import java.util.*;

public class NpcImpl extends Npc {

    private final String internalName;
    private final UUID tabEntryUUID;
    private final NpcAttribute playerPoseAttr;
    private FS_Entity npc;
    private FS_TextDisplay sittingVehicle;

    public NpcImpl(NpcData data) {
        super(data);
        this.playerPoseAttr = FancyNpcsPlugin.get().getAttributeManager().getAttributeByName(org.bukkit.entity.EntityType.PLAYER, "pose");

        this.internalName = generateLocalName();
        this.tabEntryUUID = UUID.randomUUID();
    }

    @Override
    public void create() {
        if (data.getType() == EntityType.PLAYER) {
            npc = new FS_Player();
        } else {
            npc = new FS_Entity(data.getType());
        }

        sittingVehicle = new FS_TextDisplay();
    }

    @Override
    public void spawn(Player player) {
        if (npc == null) {
            return;
        }

        if (!data.getLocation().getWorld().getName().equalsIgnoreCase(player.getLocation().getWorld().getName())) {
            return;
        }

        FS_RealPlayer fsPlayer = new FS_RealPlayer(player);

        NpcSpawnEvent spawnEvent = new NpcSpawnEvent(this, player);
        spawnEvent.callEvent();
        if (spawnEvent.isCancelled()) {
            return;
        }

        if (npc instanceof FS_Player) {
            Map<String, FS_GameProfile.Property> properties = new HashMap<>();
            if (data.getSkin() != null) {
                String skinValue = data.getSkin().value();
                String skinSignature = data.getSkin().signature();

                if (skinValue == null || skinSignature == null) {
                    return;
                }

                properties.put("textures", new FS_GameProfile.Property("textures", skinValue, skinSignature));
            }

            FancySitula.PACKET_FACTORY.createPlayerInfoUpdatePacket(
                    EnumSet.of(
                            FS_ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER,
                            FS_ClientboundPlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME
                    ),
                    List.of(new FS_ClientboundPlayerInfoUpdatePacket.Entry(
                            npc.getUuid(),
                            new FS_GameProfile(
                                    npc.getUuid(),
                                    internalName,
                                    properties
                            ),
                            false,
                            420,
                            FS_GameType.CREATIVE,
                            Component.empty()
                    ))
            ).send(fsPlayer);
            System.out.println("create player");
        }

        if (data.isShowInTab()) {
            FancySitula.PACKET_FACTORY.createPlayerInfoUpdatePacket(
                    EnumSet.of(
                            FS_ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER,
                            FS_ClientboundPlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME
                    ),
                    List.of(new FS_ClientboundPlayerInfoUpdatePacket.Entry(
                            tabEntryUUID,
                            new FS_GameProfile(tabEntryUUID, ""),
                            false,
                            420,
                            FS_GameType.CREATIVE,
                            ModernChatColorHandler.translate(data.getDisplayName())
                    ))
            ).send(fsPlayer);
        }

        npc.setLocation(data.getLocation());
        FancySitula.ENTITY_FACTORY.spawnEntityFor(fsPlayer, npc);
        System.out.println("spawn entity");

        isVisibleForPlayer.put(player.getUniqueId(), true);

        update(player);
    }

    @Override
    public void remove(Player player) {
        if (npc == null) {
            return;
        }

        FS_RealPlayer fsPlayer = new FS_RealPlayer(player);

        if (npc instanceof FS_Player) {
            FancySitula.PACKET_FACTORY
                    .createPlayerInfoRemovePacket(npc.getUuid())
                    .send(fsPlayer);
        }

        FancySitula.PACKET_FACTORY
                .createPlayerInfoRemovePacket(tabEntryUUID)
                .send(fsPlayer);

        FancySitula.ENTITY_FACTORY.despawnEntityFor(fsPlayer, npc);
        FancySitula.ENTITY_FACTORY.despawnEntityFor(fsPlayer, sittingVehicle);

        isVisibleForPlayer.put(player.getUniqueId(), false);
    }

    @Override
    public void lookAt(Player player, Location location) {
        if (npc == null) {
            return;
        }

        FS_RealPlayer fsPlayer = new FS_RealPlayer(player);

        FancySitula.PACKET_FACTORY.createTeleportEntityPacket(
                npc.getId(),
                data.getLocation().getX(),
                data.getLocation().getY(),
                data.getLocation().getZ(),
                player.getLocation().getYaw(),
                player.getLocation().getPitch(),
                true
        ).send(fsPlayer);

        FancySitula.PACKET_FACTORY.createRotateHeadPacket(
                npc.getId(),
                player.getLocation().getYaw()
        ).send(fsPlayer);
    }

    @Override
    public void update(Player player) {
        if (npc == null) {
            return;
        }

        if (!isVisibleForPlayer.getOrDefault(player.getUniqueId(), false)) {
            return;
        }

        FS_RealPlayer fsPlayer = new FS_RealPlayer(player);

        FS_Team team = new FS_Team(
                "fancynpcs-" + npc.getUuid().toString(),
                Component.empty(),
                true,
                true,
                data.getDisplayName().equalsIgnoreCase("<empty>") ? FS_NameTagVisibility.NEVER : FS_NameTagVisibility.ALWAYS,
                data.isCollidable() ? FS_CollisionRule.ALWAYS : FS_CollisionRule.NEVER,
                FS_Color.AQUA, //TODO use glowing color
                ModernChatColorHandler.translate(data.getDisplayName()),
                Component.empty(),
                new ArrayList<>()
        );
        if (npc instanceof FS_Player) {
            team.setEntities(List.of(internalName));
        } else {
            team.setEntities(List.of(npc.getUuid().toString()));
        }

        boolean isTeamCreated = getIsTeamCreated().getOrDefault(player.getUniqueId(), false);
        if (!isTeamCreated) {
            FancySitula.TEAM_FACTORY.createTeamFor(fsPlayer, team);
            getIsTeamCreated().put(player.getUniqueId(), true);
        } else {
            FancySitula.TEAM_FACTORY.updateTeamFor(fsPlayer, team);
        }

        // displayname for non-player entities
        if (!(npc instanceof FS_Player)) {
            npc.setCustomNameVisible(true);
            npc.setCustomName(Optional.of(ModernChatColorHandler.translate(data.getDisplayName())));
        }

        if (data.isGlowing()) {
            npc.setSharedFlags((byte) 0x40); // glowing bit
        }

        if (data.getEquipment() != null && !data.getEquipment().isEmpty()) {
            Map<FS_EquipmentSlot, ItemStack> equipment = new HashMap<>();
            data.getEquipment().forEach((slot, item) -> {
                equipment.put(FS_EquipmentSlot.valueOf(slot.name()), item);
            });
            FancySitula.PACKET_FACTORY
                    .createSetEquipmentPacket(npc.getId(), equipment)
                    .send(fsPlayer);
        }


        if (npc instanceof FS_Player) {
            // TODO enable second skin layer
            //npc.getEntityData().set(net.minecraft.world.entity.player.Player.DATA_PLAYER_MODE_CUSTOMISATION, (byte) (0x01 | 0x02 | 0x04 | 0x08 | 0x10 | 0x20 | 0x40));
        }

        // TODO apply attributes

        FancySitula.ENTITY_FACTORY.setEntityDataFor(fsPlayer, npc);

        if (data.isSpawnEntity() && data.getLocation() != null) {
            move(player, true);
        }

        if (data.getAttributes().containsKey(playerPoseAttr)) {
            String pose = data.getAttributes().get(playerPoseAttr);
            if (pose.equals("sitting")) {
                setSitting(fsPlayer);
            } else {
                FancySitula.PACKET_FACTORY
                        .createRemoveEntitiesPacket(List.of(sittingVehicle.getId()))
                        .send(fsPlayer);
            }

        }

        if (data.getScale() > 0) {
            // TODO set scale attribute
        }

        move(player, false);
    }

    @Override
    public void move(Player player, boolean swingArm) {
        if (npc == null) {
            return;
        }

        FS_RealPlayer fsPlayer = new FS_RealPlayer(player);

        FancySitula.PACKET_FACTORY.createTeleportEntityPacket(
                sittingVehicle.getId(),
                data.getLocation().getX(),
                data.getLocation().getY(),
                data.getLocation().getZ(),
                data.getLocation().getYaw(),
                data.getLocation().getPitch(),
                true
        ).send(fsPlayer);

        FancySitula.PACKET_FACTORY.createTeleportEntityPacket(
                npc.getId(),
                data.getLocation().getX(),
                data.getLocation().getY(),
                data.getLocation().getZ(),
                data.getLocation().getYaw(),
                data.getLocation().getPitch(),
                true
        ).send(fsPlayer);

        float angelMultiplier = 256f / 360f;
        FancySitula.PACKET_FACTORY.createRotateHeadPacket(
                npc.getId(),
                data.getLocation().getYaw() * angelMultiplier
        ).send(fsPlayer);

        if (swingArm) {
            // TODO swing arm animation
        }
    }

    /**
     * Use {@link #update(Player)} instead
     */
    @Deprecated()
    @Override
    protected void refreshEntityData(Player serverPlayer) {
    }

    @Override
    public int getEntityId() {
        if (npc == null) {
            return -1;
        }

        return npc.getId();
    }

    @Deprecated
    @Override
    public float getEyeHeight() {
        //TODO get rid of this method
        return 0;
    }

    private void setSitting(FS_RealPlayer player) {
        if (npc == null || sittingVehicle == null) {
            return;
        }

        sittingVehicle.setLocation(data.getLocation());

        FancySitula.ENTITY_FACTORY.spawnEntityFor(player, sittingVehicle);

        FancySitula.PACKET_FACTORY.createSetPassengersPacket(
                sittingVehicle.getId(),
                List.of(npc.getId())
        ).send(player);
    }
}
