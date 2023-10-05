package org.dawnoftimevillage.world.village;

import net.minecraft.core.BlockPos;
import org.apache.commons.compress.utils.Lists;
import org.dawnoftimevillage.world.building.Building;
import org.dawnoftimevillage.world.buildingsite.BuildingSite;
import org.dawnoftimevillage.world.entity.Culture;
import org.dawnoftimevillage.world.entity.DoTVillager;

import java.util.List;

public class Village {
    private List<Building> buildings = Lists.newArrayList();
    private List<BuildingSite> buildingSites = Lists.newArrayList();
    private List<DoTVillager> villagers = Lists.newArrayList();
    private Culture culture;
    private String name;
    private BlockPos centerPos;
    private boolean loaded;

}
