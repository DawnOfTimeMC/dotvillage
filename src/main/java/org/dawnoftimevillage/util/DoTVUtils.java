package org.dawnoftimevillage.util;

import net.minecraft.resources.ResourceLocation;
import org.dawnoftimevillage.DawnOfTimeVillage;

public class DoTVUtils {
    public static ResourceLocation resource(String name) {
        return new ResourceLocation(DawnOfTimeVillage.MOD_ID, name);
    }
}
