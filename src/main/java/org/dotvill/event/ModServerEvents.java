package org.dotvill.event;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.dotvill.DotVill;
import org.dotvill.culture.CultureManager;
import org.dotvill.entity.Dillager;
import org.dotvill.registry.ModCommands;
import org.dotvill.util.ModLogger;
import org.dotvill.village.VillageManager;

@Mod.EventBusSubscriber(modid = DotVill.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModServerEvents {

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
        ModCommands.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void finalizeSpawn(MobSpawnEvent.FinalizeSpawn event) {
        if (event.getEntity() instanceof Dillager villager) {
            ModLogger.info("test");
            ModLogger.LOGGER.info("test2");
            villager.onFinalizeSpawnEvent();
        }
    }
}
