package org.dawnoftimevillage.event;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.dawnoftimevillage.DawnOfTimeVillage;
import org.dawnoftimevillage.entity.DotVillager;
import org.dawnoftimevillage.registry.DotvCommands;
import org.dawnoftimevillage.registry.DotvEntities;
import org.dawnoftimevillage.util.DotvLogger;
import org.dawnoftimevillage.util.DotvUtils;
import org.dawnoftimevillage.village.VillageManager;
import org.dawnoftimevillage.village.capability.IVillageList;
import org.dawnoftimevillage.village.capability.VillageListProvider;

@Mod.EventBusSubscriber(modid = DawnOfTimeVillage.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DotvServerHandler {
    @SubscribeEvent
    public static void commonSetup(FMLCommonSetupEvent event) {
        /*
        event.enqueueWork(() -> {
            // Packet Handler init here
        }); */
    }

    @SubscribeEvent
    public static void onChunkUnloaded(ChunkEvent.Unload event) {
        // see if we need to unload village
    }

    @SubscribeEvent
    public static void onServerLevelTick(TickEvent.LevelTickEvent event) {
        if (event.level instanceof ServerLevel serverLevel) {
            VillageManager.tickVillages((serverLevel));
        }
    }

    /*
    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(IVillageList.class);
    }

     */

    /*
    @SubscribeEvent
    public static void attachEntityCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player player) {
            if (player.getCapability(PlayerKnowledgeProvider.PLAYER_KNOWLEDGE).isPresent()) {
                event.addCapability(DotvUtils.resource("properties"), new PlayerKnowledgeProvider());
            }
        }
    }
     */

    /*
    @SubscribeEvent
    public static void attachLevelCapabilities(AttachCapabilitiesEvent<Level> event) {
        event.addCapability(DotvUtils.resource("villagelist"), new VillageListProvider());

    }

     */

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        DotvCommands.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void createEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(DotvEntities.DOT_VILLAGER.get(), DotVillager.createAttributes().build());
    }

    @SubscribeEvent
    public static void finalizeSpawn(MobSpawnEvent.FinalizeSpawn event) {
        if (event.getEntity() instanceof DotVillager villager) {
            DotvLogger.info("test");
            DotvLogger.LOGGER.info("test2");
            villager.onFinalizeSpawnEvent();
        }
    }
}
