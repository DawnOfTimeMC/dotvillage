package org.dawnoftimevillage.construction.project;

import net.minecraft.server.level.ServerLevel;
import org.dawnoftimevillage.building.Building;
import org.dawnoftimevillage.construction.BuildingPlacementSettings;

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
