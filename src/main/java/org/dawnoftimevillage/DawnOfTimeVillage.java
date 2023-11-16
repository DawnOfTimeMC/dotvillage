package org.dawnoftimevillage;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.dawnoftimevillage.event.DotvClientHandler;
import org.dawnoftimevillage.event.DotvServerHandler;
import org.dawnoftimevillage.registry.DotvEntities;
import org.dawnoftimevillage.registry.DotvItems;
import org.dawnoftimevillage.registry.DotvStructures;

@Mod(DawnOfTimeVillage.MOD_ID)
public class DawnOfTimeVillage {
    public static final String MOD_ID = "dawnoftimevillage";

    public DawnOfTimeVillage() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        DotvEntities.ENTITY_TYPES.register(modEventBus);
        DotvStructures.STRUCTURE_TYPES.register(modEventBus);
        DotvStructures.STRUCTURE_PIECES.register(modEventBus);
        DotvItems.ITEMS.register(modEventBus);

        // Mod bus common events
        modEventBus.addListener(DotvServerHandler::commonSetup);
        modEventBus.addListener(DotvServerHandler::createEntityAttributes);
        // Mod bus client events
        modEventBus.addListener(DotvClientHandler::addCreative);
        modEventBus.addListener(DotvClientHandler::clientSetup);
        modEventBus.addListener(DotvClientHandler::registerRenderers);
        modEventBus.addListener(DotvClientHandler::registerLayerDefinitions);
    }
}
