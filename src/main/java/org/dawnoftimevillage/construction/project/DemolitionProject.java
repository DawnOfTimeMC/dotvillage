package org.dawnoftimevillage.construction.project;

import net.minecraft.server.level.ServerLevel;
import org.dawnoftimevillage.building.Building;
import org.dawnoftimevillage.construction.BuildingPlacementSettings;

public class DemolitionProject extends ConstructionProject {
    private Building toDemolish;
    private RemovingEntitiesPhase phase1;
    private RemovingBlocksPhase phase2;

    protected DemolitionProject(ServerLevel level, String name, BuildingPlacementSettings settings) {
        super(level, name, settings);
    }
}
