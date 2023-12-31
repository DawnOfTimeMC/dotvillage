package org.dotvill;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.dotvill.client.model.DillagerModel;
import org.dotvill.client.renderer.DillagerRenderer;
import org.dotvill.client.screen.BuyScreen;
import org.dotvill.client.screen.SellScreen;
import org.dotvill.client.screen.tooltip.ClientTradeItemTooltip;
import org.dotvill.client.screen.tooltip.TradeItemTooltip;
import org.dotvill.entity.Dillager;
import org.dotvill.network.ModNetwork;
import org.dotvill.registry.*;

@Mod(DotVill.MOD_ID)
public class DotVill {
    public static final String MOD_ID = "dotvill";

    public DotVill() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        MotEntities.ENTITY_TYPES.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModCreativeTabs.CREATIVE_TABS.register(modEventBus);
        ModMenus.MENUS.register(modEventBus);
        ModStructures.STRUCTURE_TYPES.register(modEventBus);
        ModStructures.STRUCTURE_PIECES.register(modEventBus);
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModBusServerEvents {
        @SubscribeEvent
        public static void commonSetup(FMLCommonSetupEvent event) {
            event.enqueueWork(ModNetwork::init);
        }

        @SubscribeEvent
        public static void createEntityAttributes(EntityAttributeCreationEvent event) {
            event.put(MotEntities.DILLAGER.get(), Dillager.createAttributes().build());
        }
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ModBusClientEvents {
        @SubscribeEvent
        public static void clientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(
                    () -> {
                        MenuScreens.register(ModMenus.BUY_MENU.get(), BuyScreen::new);
                        MenuScreens.register(ModMenus.SELL_MENU.get(), SellScreen::new);
                    }
            );
        }

        @SubscribeEvent
        public static void addCreative(BuildCreativeModeTabContentsEvent event) {
            if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
                event.accept(ModItems.DILLAGER_SPAWN_EGG);
            }
            if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
                event.accept(ModItems.EMERALD_SHARD);
            }
        }

        @SubscribeEvent
        public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(MotEntities.DILLAGER.get(), DillagerRenderer::new);
        }

        @SubscribeEvent
        public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
            event.registerLayerDefinition(DillagerModel.LAYER_LOCATION, DillagerModel::createBodyLayer);
        }

        @SubscribeEvent
        public static void registerToolTips(RegisterClientTooltipComponentFactoriesEvent event) {
            event.register(TradeItemTooltip.class, ClientTradeItemTooltip::new);
        }
    }
}
