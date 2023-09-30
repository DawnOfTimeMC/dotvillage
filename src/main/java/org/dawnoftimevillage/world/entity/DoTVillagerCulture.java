package org.dawnoftimevillage.world.entity;

import com.google.common.collect.Maps;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

import java.util.Map;

public enum DoTVillagerCulture {
    DESERT, JUNGLE, PLAINS, SAVANNA, SNOW, SWAMP, TAIGA;

    private static final Map<ResourceKey<Biome>, DoTVillagerCulture> BY_BIOME = Util.make(Maps.newHashMap(), (p) -> {
        p.put(Biomes.BADLANDS, DESERT);
        p.put(Biomes.DESERT, DESERT);
        p.put(Biomes.ERODED_BADLANDS, DESERT);
        p.put(Biomes.WOODED_BADLANDS, DESERT);
        p.put(Biomes.BAMBOO_JUNGLE, JUNGLE);
        p.put(Biomes.JUNGLE, JUNGLE);
        p.put(Biomes.SPARSE_JUNGLE, JUNGLE);
        p.put(Biomes.SAVANNA_PLATEAU, SAVANNA);
        p.put(Biomes.SAVANNA, SAVANNA);
        p.put(Biomes.WINDSWEPT_SAVANNA, SAVANNA);
        p.put(Biomes.DEEP_FROZEN_OCEAN, SNOW);
        p.put(Biomes.FROZEN_OCEAN, SNOW);
        p.put(Biomes.FROZEN_RIVER, SNOW);
        p.put(Biomes.ICE_SPIKES, SNOW);
        p.put(Biomes.SNOWY_BEACH, SNOW);
        p.put(Biomes.SNOWY_TAIGA, SNOW);
        p.put(Biomes.SNOWY_PLAINS, SNOW);
        p.put(Biomes.GROVE, SNOW);
        p.put(Biomes.SNOWY_SLOPES, SNOW);
        p.put(Biomes.FROZEN_PEAKS, SNOW);
        p.put(Biomes.JAGGED_PEAKS, SNOW);
        p.put(Biomes.SWAMP, SWAMP);
        p.put(Biomes.MANGROVE_SWAMP, SWAMP);
        p.put(Biomes.OLD_GROWTH_SPRUCE_TAIGA, TAIGA);
        p.put(Biomes.OLD_GROWTH_PINE_TAIGA, TAIGA);
        p.put(Biomes.WINDSWEPT_GRAVELLY_HILLS, TAIGA);
        p.put(Biomes.WINDSWEPT_HILLS, TAIGA);
        p.put(Biomes.TAIGA, TAIGA);
        p.put(Biomes.WINDSWEPT_FOREST, TAIGA);
    });

    public byte getId() {
        return (byte) this.ordinal();
    }

    public String toString() {
        return name().toLowerCase();
    }

    public static DoTVillagerCulture byId(byte id) {
        for(DoTVillagerCulture culture : DoTVillagerCulture.values()) {
            if (id == culture.getId()) {
                return culture;
            }
        }
        return PLAINS;
    }

    public static DoTVillagerCulture byBiome(Holder<Biome> holder) {
        return holder.unwrapKey().map(BY_BIOME::get).orElse(PLAINS);
    }
}
