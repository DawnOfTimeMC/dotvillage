package org.dawnoftimevillage;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.dawnoftimevillage.registry.DoTVEntitiesRegistry;

@Mod(DawnOfTimeVillage.MOD_ID)
public class DawnOfTimeVillage {
    public static final String MOD_ID = "dawnoftimevillage";

    public DawnOfTimeVillage() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        DoTVEntitiesRegistry.ENTITY_TYPES.register(modEventBus);

        // Mod bus common events
        modEventBus.addListener(DoTVHandlerCommon::commonSetup);
        modEventBus.addListener(DoTVHandlerCommon::createEntityAttributes);
        // Mod bus client events
        modEventBus.addListener(DoTVHandlerClient::clientSetup);
        modEventBus.addListener(DoTVHandlerClient::registerRenderers);
        modEventBus.addListener(DoTVHandlerClient::registerLayerDefinitions);
    }
}
