package org.dawnoftimevillage.registry;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.dawnoftimevillage.DawnOfTimeVillage;
import org.dawnoftimevillage.util.DoTVUtils;
import org.dawnoftimevillage.world.entity.DoTVillager;

public class DoTVEntitiesRegistry {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, DawnOfTimeVillage.MOD_ID);

    public static final RegistryObject<EntityType<DoTVillager>> DOT_VILLAGER = ENTITY_TYPES.register("villager", () -> EntityType.Builder
            .of(DoTVillager::new, MobCategory.MISC)
            .sized(0.6F, 1.95F) // Hitbox size
            .clientTrackingRange(10) // Vanilla villagers value
            // .updateInterval() See later, default : 3 ticks
            .build(DoTVUtils.resource("villager").toString())
    );
}