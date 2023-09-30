package org.dawnoftimevillage.world.village;

import org.apache.commons.compress.utils.Lists;
import org.dawnoftimevillage.world.building.Building;
import org.dawnoftimevillage.world.buildingsite.BuildingSite;
import org.dawnoftimevillage.world.entity.DoTVillager;

import java.util.List;

public class Village {
    private List<Building> buildings = Lists.newArrayList();
    private List<BuildingSite> buildingSites = Lists.newArrayList();
    private List<DoTVillager> villagers = Lists.newArrayList();
}
