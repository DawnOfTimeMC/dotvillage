package org.dawnoftimevillage.world.buildingsite;

import com.google.common.collect.Lists;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.List;

public class BuildingSitesManager extends SavedData {
    private final ServerLevel level;
    private final List<BuildingSite> sites = Lists.newArrayList();

    public static BuildingSitesManager get(ServerLevel level) {
        if (level.isClientSide()) {
            throw new RuntimeException("BuildingSitesManager can't be accessed from client side");
        }
        DimensionDataStorage storage = level.getDataStorage();
        // First argument is load function, second is create function
        return storage.computeIfAbsent((tag) -> new BuildingSitesManager(level, tag), () -> new BuildingSitesManager(level), "buildingsites");
    }

    // Creating the manager if it doesn't exist yet
    private BuildingSitesManager(ServerLevel level) {
        this.level = level;
        setDirty();
    }

    // Loading already saved data in the manager
    private BuildingSitesManager(ServerLevel level, CompoundTag tag) {
        this(level);
        ListTag listtag = tag.getList("BuildingSites", 10);
        for(int i = 0; i < listtag.size(); ++i) {
            CompoundTag siteTag = listtag.getCompound(i);
            addSite(level, siteTag);
        }
    }

    public void onConstructionSiteCompleted(BuildingSite site) {
        removeSite(site);
    }

    public BuildingSite getSiteByName(String name) {
        for (BuildingSite site : this.sites) {
            if (site.getName().equals(name)) {
                return site;
            }
        }
        return null;
    }

    public CompoundTag save(CompoundTag tag) {
        ListTag listtag = new ListTag();
        for(BuildingSite site : this.sites) {
            CompoundTag siteTag = new CompoundTag();
            site.save(siteTag);
            listtag.add(siteTag);
        }

        tag.put("BuildingSites", listtag);
        return tag;
    }

    public void addSite(ServerLevel level, String name, ResourceLocation structureNbtFile, BuildingSiteSettings settings) {
        BuildingSite site = new BuildingSite(level, name, structureNbtFile, settings);
        if (!this.sites.contains(site)) {
            this.sites.add(site);
        }
        setDirty();
    }

    public void addSite(ServerLevel level, CompoundTag tag) {
        BuildingSite site = new BuildingSite(level, tag);
        if (!this.sites.contains(site)) {
            this.sites.add(site);
        }
        setDirty();
    }

    public void removeSite(BuildingSite site) {
        this.sites.remove(site);
        setDirty();
    }

    public List<BuildingSite> getSites() {return sites;}
}
