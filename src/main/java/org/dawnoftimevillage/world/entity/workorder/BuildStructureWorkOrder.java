package org.dawnoftimevillage.world.entity.workorder;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import org.dawnoftimevillage.world.buildingsite.BuildingSite;
import org.dawnoftimevillage.world.buildingsite.BuildingSitesManager;

public class BuildStructureWorkOrder extends WorkOrder {
    BuildingSite requestedSite;

    public BuildStructureWorkOrder(BuildingSite requestedSite) {
        this.requestedSite = requestedSite;
    }

    public BuildStructureWorkOrder(ServerLevel level, CompoundTag tag) {
        BuildingSitesManager manager = BuildingSitesManager.get(level);
        BuildingSite site = manager.getSiteByName(tag.getString("Name"));
        if (site != null) {
            this.requestedSite = site;
        }
    }

    public void notifyFinished() {
        this.requestedSite = null;
    }

    public CompoundTag save(CompoundTag tag) {
        this.requestedSite.save(tag);
        return tag;
    }

    public BuildingSite getRequestedSite() {return this.requestedSite;}
}
