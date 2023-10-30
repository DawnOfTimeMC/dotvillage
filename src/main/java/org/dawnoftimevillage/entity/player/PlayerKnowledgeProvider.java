package org.dawnoftimevillage.entity.player;

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

public class PlayerKnowledgeProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static Capability<PlayerKnowledge> PLAYER_KNOWLEDGE = CapabilityManager.get(new CapabilityToken<>() {});
    private PlayerKnowledge playerKnowledge = null;
    private final LazyOptional<PlayerKnowledge> optional = LazyOptional.of(this::createPlayerKnowledge);

    private PlayerKnowledge createPlayerKnowledge() {
        if (this.playerKnowledge == null) {
            this.playerKnowledge = new PlayerKnowledge();
        }
        return playerKnowledge;
    }

    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == PLAYER_KNOWLEDGE) {
            return optional.cast();
        }
        return LazyOptional.empty();
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        createPlayerKnowledge().save(tag);
        return tag;
    }

    public void deserializeNBT(CompoundTag tag) {
        createPlayerKnowledge().load(tag);
    }
}
