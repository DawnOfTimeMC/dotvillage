package org.dawnoftimevillage.village.capability;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import org.dawnoftimevillage.village.Village;

import java.util.ArrayList;
import java.util.List;

public class VillageList implements IVillageList {
    private List<Village> villages = new ArrayList<>();

    public CompoundTag serializeNBT() {
        ListTag villagesTag = new ListTag();
        for (Village village : this.villages) {
            CompoundTag villageTag = new CompoundTag();
            village.saveNBT(villageTag);
            villagesTag.add(villagesTag);
        }
        CompoundTag tag = new CompoundTag();
        tag.put("Villages", villagesTag);
        return tag;
    }

    public void deserializeNBT(CompoundTag tag) {
        ListTag villagesTag = tag.getList("Villages", 10);
        for(int i = 0; i < villagesTag.size(); ++i) {
            CompoundTag villageTag = villagesTag.getCompound(i);
            loadVillage(villageTag);
        }
    }

    private void loadVillage(CompoundTag tag) {
        //this.villages.add(Village.loadVillage(tag));
    }

    public List<Village> getVillages() {
        return this.villages;
    }

    public boolean addVillage(Village village) {
        return this.villages.add(village);
    }

    public boolean removeVillage(Village village) {
        return this.villages.remove(village);
    }
}
