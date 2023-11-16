package org.dawnoftimevillage.village;

import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.dawnoftimevillage.culture.Culture;

import java.util.List;

public class VillageManager {
    /**
     * Villages will be ticked every 5 seconds
     */
    public static int VILLAGE_TICK_RATE = SharedConstants.TICKS_PER_SECOND * 5;

    public static Village addVillage(ServerLevel level, BlockPos position, Culture culture) {
        VillageSavedData savedData = VillageSavedData.get(level);
        if (savedData != null) {
            Village village = Village.create(level, position);
            savedData.addVillage(village);
            return village;
        } else {
            return null;
        }
    }

    public static void removeVillage(int villageID) {

    }

    public static List<Village> getVillageList(ServerLevel level) {
        VillageSavedData savedData = VillageSavedData.get(level);
        if (savedData != null) {
            return savedData.getVillages();
        } else {
            return null;
        }
    }

    public static void tickVillages(ServerLevel level) {
        if (level.getServer().getTickCount() % VILLAGE_TICK_RATE == 0) {
            List<Village> villages = getVillageList(level);
            if (villages != null) {
                for (Village village : villages) {
                    if (village.isActive()) {
                        village.activeVillageTick();
                    } else {
                        village.inactiveVillageTick();
                    }
                }
            }
        }
    }
}

