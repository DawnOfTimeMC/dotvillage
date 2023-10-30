package org.dawnoftimevillage.registry;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.dawnoftimevillage.DawnOfTimeVillage;
import org.dawnoftimevillage.entity.DotVillager;
import org.dawnoftimevillage.util.DotvUtils;

public class DotvEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, DawnOfTimeVillage.MOD_ID);

    public static final RegistryObject<EntityType<DotVillager>> DOT_VILLAGER = ENTITY_TYPES.register("villager", () -> EntityType.Builder
            .of(DotVillager::new, MobCategory.MISC)
            .sized(0.6F, 1.95F) // Hitbox size
            .clientTrackingRange(10) // Vanilla villagers value
            // .updateInterval() See later, default : 3 ticks
            .build(DotvUtils.resource("villager").toString())
    );
}