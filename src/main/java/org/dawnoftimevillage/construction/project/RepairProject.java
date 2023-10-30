package org.dawnoftimevillage.construction.project;

import net.minecraft.server.level.ServerLevel;
import org.dawnoftimevillage.building.Building;
import org.dawnoftimevillage.construction.BuildingPlacementSettings;

public class RepairProject extends ConstructionProject{
    private Building toRepair;

    private RemovingEntitiesPhase phase1;
    private RemovingBlocksPhase phase2;
    private PlacingBlocksPhase phase3;
    private PlacingEntitiesPhase phase4;

    protected RepairProject(ServerLevel level, String name, BuildingPlacementSettings settings) {
        super(level, name, settings);
    }
}
