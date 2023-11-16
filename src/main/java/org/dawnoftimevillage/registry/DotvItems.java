package org.dawnoftimevillage.registry;

import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.dawnoftimevillage.DawnOfTimeVillage;

public class DotvItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, DawnOfTimeVillage.MOD_ID);

    public static final RegistryObject<Item> DOT_VILLAGER_SPAWN_EGG = ITEMS.register("villager_spawn_egg", () ->
            new ForgeSpawnEggItem(DotvEntities.DOT_VILLAGER, 0x96691f, 0x38b934/*51A03E*/, new Item.Properties()));
}
