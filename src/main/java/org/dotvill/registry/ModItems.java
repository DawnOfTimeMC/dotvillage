package org.dotvill.registry;

import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.dotvill.DotVill;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, DotVill.MOD_ID);

    public static final RegistryObject<Item> DILLAGER_SPAWN_EGG = ITEMS.register("villager_spawn_egg", () ->
            new ForgeSpawnEggItem(MotEntities.DILLAGER, 0x96691f, 0x38b934/*51A03E*/, new Item.Properties()));

    public static final RegistryObject<Item> EMERALD_SHARD = ITEMS.register("emerald_shard", () ->
            new Item(new Item.Properties()));
}