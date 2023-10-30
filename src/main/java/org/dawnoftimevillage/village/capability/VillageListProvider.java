package org.dawnoftimevillage.village.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VillageListProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static final Capability<IVillageList> VILLAGE_LIST_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
    private final IVillageList villageList = new VillageList();
    private final LazyOptional<IVillageList> optional = LazyOptional.of(() -> this.villageList);

    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == VILLAGE_LIST_CAPABILITY ? this.optional.cast() : LazyOptional.empty();
    }

    public CompoundTag serializeNBT() {
        return this.villageList.serializeNBT();
    }

    public void deserializeNBT(CompoundTag nbt) {
        this.villageList.deserializeNBT(nbt);
    }
}
