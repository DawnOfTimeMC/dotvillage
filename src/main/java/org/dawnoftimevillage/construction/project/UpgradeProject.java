package org.dawnoftimevillage.construction.project;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import org.dawnoftimevillage.building.Building;
import org.dawnoftimevillage.construction.BuildingPlacementSettings;
import org.dawnoftimevillage.construction.ConstructionPlan;
import org.dawnoftimevillage.util.DotvUtils;

public class UpgradeProject extends ConstructionProject{
    private Building toUpgrade;

    private RemovingEntitiesPhase phase1;
    private RemovingBlocksPhase phase2;
    private PlacingBlocksPhase phase3;
    private PlacingEntitiesPhase phase4;

    protected UpgradeProject(ServerLevel level, String name, BuildingPlacementSettings settings) {
        super(level, name, settings);
    }
}
