package org.dawnoftimevillage.event;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.dawnoftimevillage.DawnOfTimeVillage;
import org.dawnoftimevillage.culture.CultureManager;
import org.dawnoftimevillage.entity.DotVillager;
import org.dawnoftimevillage.registry.DotvCommands;
import org.dawnoftimevillage.registry.DotvEntities;
import org.dawnoftimevillage.util.DotvLogger;
import org.dawnoftimevillage.village.Village;
import org.dawnoftimevillage.village.VillageManager;

import java.util.List;

@Mod.EventBusSubscriber(modid = DawnOfTimeVillage.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DotvServerHandler {
    @SubscribeEvent
    public static void commonSetup(FMLCommonSetupEvent event) {
        /*
        event.enqueueWork(() -> {
            // Packet Handler init here
        }); */
    }

    /*
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!event.player.level().isClientSide()) {
            List<Village> villages = VillageManager.getVillageList((ServerLevel)event.player.level());
            for (Village village : villages) {
                BlockPos villagePos = village.getCenterPosition();
                BlockPos playerPos = event.player.blockPosition();
                if (playerPos.closerThan(villagePos, 100D)) {
                    village.setActive();
                }
            }
        }
    } */

    /*
    @SubscribeEvent
    public static void onChunkUnloaded(ChunkEvent.Unload event) {
        if (event.getLevel() instanceof ServerLevel level) {
            List<Village> villages = VillageManager.getVillageList(level);
            if (villages != null) {
                for (Village village : villages) {
                    level.getChunk(village.getCenterPosition());
                }
            }

        }
    } */

    @SubscribeEvent
    public static void addReloadListener(AddReloadListenerEvent event) {
        event.addListener(CultureManager.instance());
    }

    @SubscribeEvent
    public static void onServerLevelTick(TickEvent.LevelTickEvent event) {
        if (!event.level.isClientSide() && event.phase.equals(TickEvent.Phase.END)) {
            //DotvLogger.info("Tick n°" + event.level.getServer().getTickCount() + " on" + event.level.dimension());
            VillageManager.tickVillages((ServerLevel) event.level);
        }
    }

    /*
    @SubscribeEvent
    public static <Entity> void attachEntityCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player player) {
            if (player.getCapability(PlayerKnowledgeProvider.PLAYER_KNOWLEDGE).isPresent()) {
                event.addCapability(DotvUtils.resource("properties"), new PlayerKnowledgeProvider());
            }
        }
    } */

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
