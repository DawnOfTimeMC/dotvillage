package org.dawnoftimevillage.village;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.dawnoftimevillage.culture.Culture;

import java.util.List;

public class VillageManager {
    public static Village addVillage(ServerLevel level, BlockPos position, Culture culture) {
        VillageSavedData savedData = VillageSavedData.get(level);
        if (savedData != null) {
            Village village = Village.create(level, position);
            savedData.addVillage(village);
            return village;
        }
        /*
        IVillageList villages = level.getCapability(VillageListProvider.VILLAGE_LIST_CAPABILITY).resolve().orElse(null);
        if (villages != null) {
            Village village = Village.create(position);
            villages.addVillage(village);
        }

         */
        return null;
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
        /*
        IVillageList villages = level.getCapability(VillageListProvider.VILLAGE_LIST_CAPABILITY).resolve().orElse(null);
        if (villages != null) {
            return villages.getVillages();
        } else {
            return null;
        }

         */
    }

    public static void tickVillages(ServerLevel level) {

        //tickOverworldVillagesOnly(level);

        /*
        for (ServerLevel serverLevel : level.getServer().getAllLevels()) {
            serverLevel.getCapability(LevelVillagesProvider.LEVEL_VILLAGES).ifPresent(villages -> {
                for (Village village : villages.getVillages()) {
                    village.tick();
                }
            });
        }
         */
    }

    public static void tickOverworldVillagesOnly(ServerLevel level) {
        if (level.dimension() == Level.OVERWORLD) {
            VillageSavedData savedData = VillageSavedData.get(level);
            if (savedData != null) {
                int times = 0;
                for (Village village : savedData.getVillages()) {
                    //DotvLogger.info(String.valueOf(times));
                    village.updateActive();
                    ++times;
                }
            }
        }
    }
}
