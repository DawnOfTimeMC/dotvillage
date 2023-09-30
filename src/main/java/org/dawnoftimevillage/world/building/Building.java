package org.dawnoftimevillage.world.building;

import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;

import java.util.List;

public class Building {
    private BlockPos location;
    private BuildingCategory category;
    private List<BlockPos> sleepLocations = Lists.newArrayList();
    private List<BlockPos> workLocations = Lists.newArrayList();

}
