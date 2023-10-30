package org.dawnoftimevillage.building;

import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import org.dawnoftimevillage.construction.BuildingPlacementSettings;
import org.dawnoftimevillage.construction.project.FreshBuildingProject;

import java.util.ArrayList;
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

    public BlockPos getPosition() {
        return position;
    }

    public ResourceLocation getStructurePath() {
        return structurePath;
    }
}
