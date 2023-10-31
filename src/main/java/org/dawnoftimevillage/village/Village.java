package org.dawnoftimevillage.village;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.dawnoftimevillage.building.Building;
import org.dawnoftimevillage.construction.project.ConstructionProject;
import org.dawnoftimevillage.culture.Culture;
import org.dawnoftimevillage.entity.DotVillager;
import org.dawnoftimevillage.util.DotvLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Village {
    private final List<Building> buildings;
    private final List<ConstructionProject> constructionProjects;
    private final List<DotVillager> villagers;
    private final Culture culture;
    private final String name;
    private final BlockPos centerPosition;
    private final Level level;
    private final VillageInventory inventory;
    private Status status = Status.INACTIVE;
    private long momentBecameInactive;
    private long lastProductionHarvest;
    public static final long HARVESTS_INTERVAL = 24000L;
    private List<ChunkPos> villageChunks;
    private ChunkPos southWestCorner;
    private ChunkPos northEastCorner;

    private Village(Level level, List<Building> buildings, List<ConstructionProject> constructionProjects, List<DotVillager> villagers, Culture culture, String name, BlockPos position, VillageInventory inventory) {
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
        List<DotVillager> villagers = new ArrayList<>();
        Culture culture = new Culture();
        String name = "village " + RandomSource.create().nextInt(0,100);
        VillageInventory inventory = new VillageInventory();

        return new Village(level, buildings, constructionProjects, villagers, culture, name, position, inventory);
    }

    static Village loadVillage(Level level, CompoundTag tag) {
        List<Building> buildings = new ArrayList<>();
        List<ConstructionProject> constructionProjects = new ArrayList<>();
        List<DotVillager> villagers = new ArrayList<>();
        Culture culture = new Culture();

        BlockPos position = NbtUtils.readBlockPos(tag.getCompound("Position"));
                //new BlockPos(tag.getInt("PosX"), tag.getInt("PosY"), tag.getInt("PosZ"));
        String name = tag.getString("Name");
        VillageInventory inventory = new VillageInventory(tag.getCompound("VillageInventory"));

        return new Village(level, buildings, constructionProjects, villagers, culture, name, position, inventory);
    }

    public void saveNBT(CompoundTag tag) {
        tag.putString("Name", this.name);
        tag.put("Position", NbtUtils.writeBlockPos(this.centerPosition));
        this.inventory.saveNBT(tag);
    }

    public void setActive() {
        this.status = Status.ACTIVE;
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
        this.status = Status.INACTIVE;
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

    public void updateActive() {
        //level.getChunk(position);
        DotvLogger.info("Ticking village \"" + this.name + "\"");
        maybeCollectProduction();
    }

    public enum Status {
        ACTIVE, INACTIVE
    }

    public String getName() {
        return this.name;
    }

    public BlockPos getCenterPosition() {
        return this.centerPosition;
    }
}
