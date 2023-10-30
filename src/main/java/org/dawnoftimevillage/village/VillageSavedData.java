package org.dawnoftimevillage.village;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.dawnoftimevillage.util.DotvLogger;

import java.util.ArrayList;
import java.util.List;

public class VillageSavedData extends SavedData {
    private final List<Village> villages = new ArrayList<>();
    private Level level;

    public static VillageSavedData get(ServerLevel level) {
        if (!level.isClientSide()) {
            DimensionDataStorage storage = level.getDataStorage();
            // First argument is load function, second is create function
            return storage.computeIfAbsent((tag) -> new VillageSavedData(level, tag), () -> new VillageSavedData(level), "dotvillages");
        } else {
            return null;
        }
    }

    private VillageSavedData(ServerLevel level) {
        this.level = level;
        //setDirty();
    }

    private VillageSavedData(ServerLevel level, CompoundTag tag) {
        this(level);
        load(tag);
    }

    public boolean isDirty() {
        return true;
    }

    public CompoundTag save(CompoundTag tag) {
        ListTag villagesTag = new ListTag();
        DotvLogger.info("before for");
        for (Village village : this.villages) {
            DotvLogger.info("inside save");
            CompoundTag villageTag = new CompoundTag();
            village.saveNBT(villageTag);
            villagesTag.add(villageTag);
        }
        DotvLogger.info("after for");
        tag.put("Villages", villagesTag);
        return tag;
    }

    public void load(CompoundTag tag) {
        DotvLogger.info("inside load 1");
        ListTag villagesTag = tag.getList("Villages", 10);
        for(int i = 0; i < villagesTag.size(); ++i) {
            DotvLogger.info("inside load 2");
            CompoundTag villageTag = villagesTag.getCompound(i);
            loadVillage(villageTag);
        }
    }

    private void loadVillage(CompoundTag tag) {
        this.villages.add(Village.loadVillage(this.level, tag));
    }

    public List<Village> getVillages() {
        return this.villages;
    }

    public boolean addVillage(Village village) {
        //setDirty();
        return this.villages.add(village);
    }

    public boolean removeVillage(Village village) {
        //setDirty();
        return this.villages.remove(village);
    }
}
