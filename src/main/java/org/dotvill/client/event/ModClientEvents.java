package org.dotvill.client.event;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import org.dotvill.DotVill;

@Mod.EventBusSubscriber(modid = DotVill.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModClientEvents {

}
