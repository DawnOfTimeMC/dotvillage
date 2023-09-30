package org.dawnoftimevillage;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.dawnoftimevillage.client.model.DoTVillagerModel;
import org.dawnoftimevillage.client.renderer.DoTVillagerRenderer;
import org.dawnoftimevillage.registry.DoTVEntitiesRegistry;

@Mod.EventBusSubscriber(modid = DawnOfTimeVillage.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DoTVHandlerClient {
    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        /*
        event.enqueueWork(() -> {
            // Register screens here
        }); */
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(DoTVEntitiesRegistry.DOT_VILLAGER.get(), DoTVillagerRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(DoTVillagerModel.LAYER_LOCATION, DoTVillagerModel::createBodyLayer);
    }
}
