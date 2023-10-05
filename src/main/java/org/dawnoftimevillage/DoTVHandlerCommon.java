package org.dawnoftimevillage;

import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.dawnoftimevillage.registry.DoTVCommandsRegistry;
import org.dawnoftimevillage.registry.DoTVEntitiesRegistry;
import org.dawnoftimevillage.world.entity.DoTVillager;

@Mod.EventBusSubscriber(modid = DawnOfTimeVillage.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DoTVHandlerCommon {
    @SubscribeEvent
    public static void commonSetup(FMLCommonSetupEvent event) {
        /*
        event.enqueueWork(() -> {
            // Packet Handler init here
        }); */
    }

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        DoTVCommandsRegistry.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void createEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(DoTVEntitiesRegistry.DOT_VILLAGER.get(), DoTVillager.createAttributes().build());
    }

    @SubscribeEvent
    public static void finalizeSpawn(MobSpawnEvent.FinalizeSpawn event) {
        if (event.getEntity() instanceof DoTVillager villager) {
            villager.onFinalizeSpawnEvent();
        }
    }
}
