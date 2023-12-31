package org.dotvill.culture;

import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class Culture {
    private String name;
    private List<ResourceLocation> buildingStarterPack;
    private List<ResourceLocation> buildingPrimaryPool;

    public Culture(String name) {
        this.name = name;
    }
}
