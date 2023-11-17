package org.dawnoftimevillage.village;

import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.dawnoftimevillage.building.Building;
import org.dawnoftimevillage.construction.project.ConstructionProject;
import org.dawnoftimevillage.culture.Culture;
import org.dawnoftimevillage.entity.DotVillager;
import org.dawnoftimevillage.util.DotvLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Village {
    private final UUID uuid;
    private final List<Building> buildings;
    private final List<ConstructionProject> constructionProjects;
    private final List<UUID> villagers;
    private final Culture culture;
    private final String name;
    private final BlockPos centerPosition;
    private final Level level;
    private final VillageInventory inventory;
    //private Status status = Status.INACTIVE;
    private long momentBecameInactive;
    private long lastProductionHarvest;
    public static final long HARVESTS_INTERVAL = 24000L;
    private List<ChunkPos> villageChunks;
    private ChunkPos southWestCorner;
    private ChunkPos northEastCorner;
    private boolean active;

    private Village(UUID uuid, Level level, List<Building> buildings, List<ConstructionProject> constructionProjects, List<UUID> villagers, Culture culture, String name, BlockPos position, VillageInventory inventory) {
        this.uuid = uuid;
        this.level = level;
        this.buildings = buildings;
        this.constructionProjects = constructionProjects;
        this.villagers = villagers;
        this.culture = culture;
        this.name = name;
        this.centerPosition = position;
        this.inventory = inventory;
    }

    static Village create(Level level, BlockPos position) {
        List<Building> buildings = new ArrayList<>();
        List<ConstructionProject> constructionProjects = new ArrayList<>();
        List<UUID> villagers = new ArrayList<>();
        Culture culture = new Culture("dummy");
        String name = "village " + RandomSource.create().nextInt(0,100);
        VillageInventory inventory = new VillageInventory();

        return new Village(Mth.createInsecureUUID(RandomSource.create()), level, buildings, constructionProjects, villagers, culture, name, position, inventory);
    }

    static Village loadVillage(Level level, CompoundTag tag) {
        List<Building> buildings = new ArrayList<>();
        List<ConstructionProject> constructionProjects = new ArrayList<>();
        List<UUID> villagers = new ArrayList<>();
        Culture culture = new Culture("dummy");

        BlockPos position = NbtUtils.readBlockPos(tag.getCompound("Position"));
                //new BlockPos(tag.getInt("PosX"), tag.getInt("PosY"), tag.getInt("PosZ"));
        String name = tag.getString("Name");

        ListTag villagersTag = tag.getList("Villagers", 10);
        for (int i = 0; i < villagersTag.size(); ++i) {
            CompoundTag villagerTag = villagersTag.getCompound(i);
            villagers.add(villagerTag.getUUID("UUID"));
            /*
            if (level instanceof ServerLevel serverLevel) {
                Entity entity = serverLevel.getEntity(villagerTag.getUUID("UUID"));
                if (entity instanceof DotVillager villager) {
                    villagers.add(villager);
                }
            }*/
        }

        UUID uuid = tag.getUUID("UUID");
        VillageInventory inventory = new VillageInventory(tag.getCompound("VillageInventory"));

        return new Village(uuid, level, buildings, constructionProjects, villagers, culture, name, position, inventory);
    }

    public void saveNBT(CompoundTag tag) {
        tag.putString("Name", this.name);
        tag.put("Position", NbtUtils.writeBlockPos(this.centerPosition));

        ListTag villagersTag = new ListTag();

        for (UUID uuid : this.villagers) {
            CompoundTag villagerTag = new CompoundTag();
            villagerTag.putUUID("UUID", uuid);
            villagersTag.add(villagerTag);
        }
        tag.put("Villagers", villagersTag);
        tag.putUUID("UUID", this.uuid);

        this.inventory.saveNBT(tag);
    }

    public void addBuilding(Building building) {
        this.buildings.add(building);
    }

    public void setActive() {
        //this.status = Status.ACTIVE;
        this.active = true;
        updateAfterInactivity();
    }

    private void updateAfterInactivity() {
        long currentTime = this.level.getGameTime();
        // Move villagers
        // Update constructions
        // Collect building production;
        maybeCollectProduction();
    }

    public void setInactive() {
        //this.status = Status.INACTIVE;
        this.active = false;
        this.momentBecameInactive = this.level.getGameTime();
    }

    private void collectProduction() {
        for (Building building : this.buildings) {
            HashMap<Item, Integer> production = building.getProduction();
            production.forEach(this.inventory::add);
        }
    }

    private void maybeCollectProduction() {
        long now = this.level.getGameTime();
        long lastHarvest = this.lastProductionHarvest;
        if (now >= lastHarvest + HARVESTS_INTERVAL) {
            int availableHarvests = (int)((now - lastHarvest) / HARVESTS_INTERVAL);
            for (int i = 0; i < availableHarvests; ++i) {
                collectProduction();
            }
            this.lastProductionHarvest = this.level.getGameTime();
        }
    }

    private void handleUnloadedVillagers() {
        List<DotVillager> unloadedVillagers = new ArrayList<>();
        for (UUID villagerUUID : this.villagers) {
            if (this.level instanceof ServerLevel serverLevel) {
                Entity entity = serverLevel.getEntity(villagerUUID);
                if (entity instanceof DotVillager villager && !serverLevel.isLoaded(villager.blockPosition())) {
                    unloadedVillagers.add(villager);
                }
            }
        }
        for (DotVillager villager : unloadedVillagers) {
            villager.sendSystemMessage(Component.literal("I am unloaded !"));
        }
    }

    public void activeVillageTick() {
        this.level.getServer().getPlayerList().broadcastSystemMessage(Component.literal("Ticking \"" + getName() + "\" in ACTIVE mode"), true);
        maybeCollectProduction();
        handleUnloadedVillagers();
        tryUpdateStatus();
    }

    public void inactiveVillageTick() {
        this.level.getServer().getPlayerList().broadcastSystemMessage(Component.literal("Ticking \"" + getName() + "\" in INACTIVE mode"), true);
        tryUpdateStatus();
    }

    public void tryUpdateStatus() {
        // Searching for nearby players. Village will become inactive if no players around
        List<Player> nearbyPlayers = this.level.getNearbyPlayers(TargetingConditions.forNonCombat(), null, AABB.ofSize(this.centerPosition.getCenter(), 200, 200, 200));
        /*
        List<ServerPlayer> players = this.level.getServer().getPlayerList().getPlayers();
        ServerPlayer dev = null;
        if (!players.isEmpty()) {
            dev = players.get(0);
        }
        if (dev != null) {
            dev.sendSystemMessage(Component.literal("Distance to village : " + dev.distanceToSqr(this.centerPosition.getCenter())));
        } */

        if (this.active) {
            if (nearbyPlayers.isEmpty()) {
                setInactive();
            }
        } else {
            if (!nearbyPlayers.isEmpty()) {
                setActive();
            }
        }
    }

    public boolean isActive() {
        return this.active;
    }

    public String getName() {
        return this.name;
    }

    public BlockPos getCenterPosition() {
        return this.centerPosition;
    }

    /* public enum Status {
        ACTIVE, INACTIVE
    } */
}
