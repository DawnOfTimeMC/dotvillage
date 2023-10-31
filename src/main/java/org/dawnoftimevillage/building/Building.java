package org.dawnoftimevillage.building;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.dawnoftimevillage.construction.BuildingPlacementSettings;
import org.dawnoftimevillage.construction.project.FreshBuildingProject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A building instance.<br>
 * May or may not be part of a village (lone building).<br>
 * @see FreshBuildingProject
 */
public class Building {
    private ResourceLocation structurePath;
    private BuildingPlacementSettings placementSettings;
    private BlockPos position;
    private BuildingCategory category;
    private List<BlockPos> sleepPositions = new ArrayList<>();
    private List<BlockPos> workPositions = new ArrayList<>();

    private HashMap<Item, Integer> production = new HashMap<>();

    public BlockPos getPosition() {
        return position;
    }

    public ResourceLocation getStructurePath() {
        return structurePath;
    }

    public HashMap<Item, Integer> getProduction() {
        return this.production;
    }
}
