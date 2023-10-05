package org.dawnoftimevillage.world.entity;

import net.minecraft.nbt.CompoundTag;
import org.dawnoftimevillage.world.buildingsite.BuildingSite;

public abstract class AdminOrder {

    public abstract CompoundTag save(CompoundTag tag);

    public static class BuildOrder extends AdminOrder {
        private BuildingSite requestedSite;

        public BuildOrder(BuildingSite site) {
            this.requestedSite = site;
        }

        public CompoundTag save(CompoundTag tag) {
            CompoundTag orderTag = new CompoundTag();
            orderTag.putString("OrderType", "Build");
            orderTag.putString("BuildingSite", this.requestedSite.getName());
            tag.put("AdminOrder", orderTag);
            return tag;
        }

        public BuildingSite getRequestedSite() {return this.requestedSite;}
    }

    public static class StopOrder extends AdminOrder {
        public CompoundTag save(CompoundTag tag) {
            return tag;
        }
    }
}
