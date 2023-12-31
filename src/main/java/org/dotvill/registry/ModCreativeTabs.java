package org.dotvill.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.dotvill.DotVill;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, DotVill.MOD_ID);

    public static final RegistryObject<CreativeModeTab> MAIN_CREATIVE_TAB = CREATIVE_TABS.register("mod_main_creative_tab", () -> CreativeModeTab.builder()
            .icon(() -> new ItemStack(Items.EMERALD))
            .title(Component.translatable("creativetab.main_tab"))
            .displayItems((parameters, outpout) -> {
                outpout.accept(ModItems.DILLAGER_SPAWN_EGG.get());
                outpout.accept(ModItems.EMERALD_SHARD.get());
            }).build());

}
