package org.dotvill.building;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.dotvill.construction.BuildingPlacementSettings;
import org.dotvill.construction.project.FreshBuildingProject;
import org.dotvill.culture.BuildingType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A building instance.<br>
 * May or may not be part of a village (lone building).<br>
 * @see FreshBuildingProject
 */
public class Building {
    private BuildingType buildingType;
    private ResourceLocation structurePath;
    private BuildingPlacementSettings placementSettings;
    private BlockPos position;
    private BuildingCategory category;
    private List<BlockPos> sleepPositions = new ArrayList<>();
    private List<BlockPos> workPositions = new ArrayList<>();

    private Building(BuildingType buildingType) {
        this.buildingType = buildingType;
    }

    public static Building create(BuildingType buildingType) {
        return new Building(buildingType);
    }

    public static Building loadBuilding() {
        return null;
    }

    public void saveNBT(CompoundTag tag) {

    }

    public BlockPos getPosition() {
        return position;
    }

    public ResourceLocation getStructurePath() {
        return structurePath;
    }

    public HashMap<Item, Integer> getProduction() {
        return this.buildingType.getProduction();
    }
}
