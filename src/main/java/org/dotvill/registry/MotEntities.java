package org.dotvill.registry;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.dotvill.DotVill;
import org.dotvill.entity.Dillager;
import org.dotvill.util.ModUtils;

public class MotEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, DotVill.MOD_ID);

    public static final RegistryObject<EntityType<Dillager>> DILLAGER = ENTITY_TYPES.register("villager", () -> EntityType.Builder
            .of(Dillager::new, MobCategory.MISC)
            .sized(0.6F, 1.95F) // Hitbox size
            .clientTrackingRange(10) // Vanilla villagers value
            // .updateInterval() See later, default : 3 ticks
            .build(ModUtils.resource("villager").toString())
    );
}