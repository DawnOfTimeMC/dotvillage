package org.dawnoftimevillage.village;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.checkerframework.checker.units.qual.C;
import org.dawnoftimevillage.building.Building;
import org.dawnoftimevillage.construction.project.ConstructionProject;
import org.dawnoftimevillage.construction.project.FreshBuildingProject;
import org.dawnoftimevillage.culture.Culture;
import org.dawnoftimevillage.entity.DotVillager;
import org.dawnoftimevillage.util.DotvLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Village {
    private final List<Building> buildings;
    private final List<ConstructionProject> constructionProjects;
    private final List<DotVillager> villagers;
    private final Culture culture;
    private final String name;
    private final BlockPos position;
    private final Level level;
    private boolean loaded;

    private Village(Level level, List<Building> buildings, List<ConstructionProject> constructionProjects, List<DotVillager> villagers, Culture culture, String name, BlockPos position) {
        this.level = level;
        this.buildings = buildings;
        this.constructionProjects = constructionProjects;
        this.villagers = villagers;
        this.culture = culture;
        this.name = name;
        this.position = position;
    }

    static Village create(Level level, BlockPos position) {
        List<Building> buildings = new ArrayList<>();
        List<ConstructionProject> constructionProjects = new ArrayList<>();
        List<DotVillager> villagers = new ArrayList<>();
        Culture culture = new Culture();
        String name = "village " + RandomSource.create().nextInt(0,100);

        return new Village(level, buildings, constructionProjects, villagers, culture, name, position);
    }

    static Village loadVillage(Level level, CompoundTag tag) {
        List<Building> buildings = new ArrayList<>();
        List<ConstructionProject> constructionProjects = new ArrayList<>();
        List<DotVillager> villagers = new ArrayList<>();
        Culture culture = new Culture();

        BlockPos position = NbtUtils.readBlockPos(tag.getCompound("Position"));
                //new BlockPos(tag.getInt("PosX"), tag.getInt("PosY"), tag.getInt("PosZ"));
        String name = tag.getString("Name");

        return new Village(level, buildings, constructionProjects, villagers, culture, name, position);
    }

    public void saveNBT(CompoundTag tag) {
        /*
        tag.putInt("PosX", this.position.getX());
        tag.putInt("PosY", this.position.getY());
        tag.putInt("PosZ", this.position.getZ()); */

        tag.putString("Name", this.name);
        tag.put("Position", NbtUtils.writeBlockPos(this.position));
    }

    public void tick() {
        DotvLogger.info("This village is ticking");
    }

    public String getName() {
        return this.name;
    }

    public BlockPos getPosition() {
        return this.position;
    }
}
