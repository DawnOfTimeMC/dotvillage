package org.dotvill.construction.project;

import net.minecraft.server.level.ServerLevel;
import org.dotvill.building.Building;
import org.dotvill.construction.BuildingPlacementSettings;

public class DemolitionProject extends ConstructionProject {
    private Building toDemolish;
    private RemovingEntitiesPhase phase1;
    private RemovingBlocksPhase phase2;

    protected DemolitionProject(ServerLevel level, String name, BuildingPlacementSettings settings) {
        super(level, name, settings);
    }
}
