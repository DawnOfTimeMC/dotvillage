package org.dawnoftimevillage.event;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.dawnoftimevillage.DawnOfTimeVillage;
import org.dawnoftimevillage.client.model.DotVillagerModel;
import org.dawnoftimevillage.client.renderer.DotVillagerRenderer;
import org.dawnoftimevillage.registry.DotvEntities;
import org.dawnoftimevillage.registry.DotvItems;

@Mod.EventBusSubscriber(modid = DawnOfTimeVillage.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DotvClientHandler {
    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        /*
        event.enqueueWork(() -> {
            // Register screens here
        }); */
    }
    @SubscribeEvent
    public static void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            event.accept(DotvItems.DOT_VILLAGER_SPAWN_EGG);
        }
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(DotvEntities.DOT_VILLAGER.get(), DotVillagerRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(DotVillagerModel.LAYER_LOCATION, DotVillagerModel::createBodyLayer);
    }
}
