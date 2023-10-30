package org.dawnoftimevillage.entity;

import net.minecraft.nbt.CompoundTag;
import org.dawnoftimevillage.construction.project.FreshBuildingProject;

public abstract class AdminOrder {

    public abstract CompoundTag save(CompoundTag tag);

    public static class BuildOrder extends AdminOrder {
        private FreshBuildingProject requestedSite;

        public BuildOrder(FreshBuildingProject site) {
            this.requestedSite = site;
        }

        public CompoundTag save(CompoundTag tag) {
            CompoundTag orderTag = new CompoundTag();
            orderTag.putString("OrderType", "Build");
            orderTag.putString("BuildingSite", this.requestedSite.getName());
            tag.put("AdminOrder", orderTag);
            return tag;
        }

        public FreshBuildingProject getRequestedSite() {return this.requestedSite;}
    }

    public static class StopOrder extends AdminOrder {
        public CompoundTag save(CompoundTag tag) {
            return tag;
        }
    }
}
