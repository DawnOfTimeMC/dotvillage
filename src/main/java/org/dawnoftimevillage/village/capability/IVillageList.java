package org.dawnoftimevillage.village.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;
import org.dawnoftimevillage.village.Village;

import java.util.List;

public interface IVillageList extends INBTSerializable<CompoundTag> {
    List<Village> getVillages();
    boolean addVillage(Village village);
    boolean removeVillage(Village village);
}
