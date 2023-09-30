package org.dawnoftimevillage.world.buildingsite;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.IdMapper;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class BuildingPlan {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final ResourceLocation structure;
    private final List<BlockInfo> blocks = Lists.newArrayList();
    private final List<EntityInfo> entities = Lists.newArrayList();
    private Vec3i dimensions = Vec3i.ZERO;

    private BuildingPlan(ResourceLocation structure) {
        this.structure = structure;
    }

    public static BuildingPlan createFromResource(ResourceLocation path, ResourceManager resourceManager) {
        FileToIdConverter converter = new FileToIdConverter("structures", ".nbt");
        ResourceLocation resourceLocation = converter.idToFile(path);
        try (InputStream inputStream = resourceManager.open(resourceLocation)) {
            CompoundTag tag = NbtIo.readCompressed(inputStream);
            BuildingPlan plan = new BuildingPlan(path);
            HolderGetter<Block> blockLookup =  BuiltInRegistries.BLOCK.asLookup();
            plan.readStructureFromNbt(blockLookup, tag);
            return plan.withoutAirBlocks();
        } catch (FileNotFoundException fileNotFoundException) {
            LOGGER.error("Structure not found {}", resourceLocation, fileNotFoundException);
            return null;
        } catch (Throwable throwable) {
            LOGGER.error("Couldn't load structure {}", resourceLocation, throwable);
            return null;
        }
    }

    private void readStructureFromNbt(HolderGetter<Block> blockGetter, CompoundTag tag) {
        /** Size **/
        ListTag sizeTag = tag.getList("size", 3);
        this.dimensions = new Vec3i(sizeTag.getInt(0), sizeTag.getInt(1), sizeTag.getInt(2));
        /** Blocks **/
        ListTag blocksTag = tag.getList("blocks", 10);
        /** Palette **/
        ListTag paletteTag;
        if (tag.contains("palettes", 9)) {
            ListTag palettesTag = tag.getList("palettes", 9);
            paletteTag = palettesTag.getList(0);
            /*
            int i = palettesTag.size();
            if (i == 0) {
                throw new IllegalStateException("No palettes");
            } else {
                paletteTag = palettesTag.getList(RandomSource.create().nextInt(i));
            } */
        } else {
            paletteTag = tag.getList("palette", 10);
        }
        /** Entities **/
        ListTag entitiesTag = tag.getList("entities", 10);
        for(int i = 0; i < entitiesTag.size(); ++i) {
            CompoundTag entityTag = entitiesTag.getCompound(i);
            ListTag posTag = entityTag.getList("pos", 6);
            Vec3 vec3 = new Vec3(posTag.getDouble(0), posTag.getDouble(1), posTag.getDouble(2));
            ListTag blockPosTag = entityTag.getList("blockPos", 3);
            BlockPos blockPos = new BlockPos(blockPosTag.getInt(0), blockPosTag.getInt(1), blockPosTag.getInt(2));
            if (entityTag.contains("nbt")) {
                CompoundTag nbtTag = entityTag.getCompound("nbt");
                this.entities.add(new EntityInfo(vec3, blockPos, nbtTag));
            }
        }
        buildBlockInfoList(blockGetter, paletteTag, blocksTag);
    }

    private void buildBlockInfoList(HolderGetter<Block> blockGetter, ListTag paletteTag, ListTag blocksTag) {
        Palette palette = new Palette();
        for(int i = 0; i < paletteTag.size(); ++i) {
            palette.addMapping(NbtUtils.readBlockState(blockGetter, paletteTag.getCompound(i)), i);
        }

        List<BlockInfo> blockInfoList = Lists.newArrayList();
        for(int i = 0; i < blocksTag.size(); ++i) {
            CompoundTag blockTag = blocksTag.getCompound(i);
            ListTag posTag = blockTag.getList("pos", 3);
            BlockPos blockPos = new BlockPos(posTag.getInt(0), posTag.getInt(1), posTag.getInt(2));
            BlockState blockState = palette.stateFor(blockTag.getInt("state"));
            CompoundTag nbtTag;
            if (blockTag.contains("nbt")) {
                nbtTag = blockTag.getCompound("nbt");
            } else {
                nbtTag = null;
            }
            BlockInfo blockInfo = new BlockInfo(blockPos, blockState, nbtTag);
            blockInfoList.add(blockInfo);
        }
        sortBlocks(blockInfoList);
        this.blocks.addAll(blockInfoList);
    }

    private void sortBlocks(List<BlockInfo> list) {
        Comparator<BlockInfo> comparator = Comparator.<BlockInfo>comparingInt((pY) -> {
            return pY.pos.getY();
        }).thenComparingInt((pX) -> {
            return pX.pos.getX();
        }).thenComparingInt((pZ) -> {
            return pZ.pos.getZ();
        });
        list.sort(comparator);
    }

    public BuildingPlan withoutAirBlocks() {
        List<BlockInfo> toRemove = Lists.newArrayList();
        for (BlockInfo info : this.blocks) {
            if (info.state.isAir()) {
                toRemove.add(info);
            }
        }
        this.blocks.removeAll(toRemove);
        return this;
    }

    public Block getBlock(int index) {
        return this.blocks.get(index).state.getBlock();
    }

    public BlockPos getBlockPos(int index) {
        return this.blocks.get(index).pos;
    }

    public BlockState getBlockState(int index) {
        return this.blocks.get(index).state;
    }

    public CompoundTag getBlockNBT(int index) {
        return this.blocks.get(index).nbt;
    }

    public int getBlocksQuantity() {
        return this.blocks.size();
    }

    public Vec3i getDimensions() {return this.dimensions;}

    public List<EntityInfo> getEntities() {
        return this.entities;
    }

    public ResourceLocation getStructure() {
        return this.structure;
    }

    public static record BlockInfo(BlockPos pos, BlockState state, CompoundTag nbt) {
        public String toString() {
            return String.format(Locale.ROOT, "<BlockInfo | %s | %s | %s>", this.pos, this.state, this.nbt);
        }
    }

    public static record EntityInfo(Vec3 pos, BlockPos blockPos, CompoundTag nbt) {
        public String toString() {
            return String.format(Locale.ROOT, "<EntityInfo | %s | %s | %s>", this.pos, this.blockPos, this.nbt);
        }
    }

    static class Palette implements Iterable<BlockState> {
        public static final BlockState DEFAULT_BLOCK_STATE = Blocks.AIR.defaultBlockState();
        private final IdMapper<BlockState> ids = new IdMapper<>(16);
        private int lastId;

        public int idFor(BlockState state) {
            int i = this.ids.getId(state);
            if (i == -1) {
                i = this.lastId++;
                this.ids.addMapping(state, i);
            }

            return i;
        }

        public BlockState stateFor(int id) {
            BlockState blockstate = this.ids.byId(id);
            return blockstate == null ? DEFAULT_BLOCK_STATE : blockstate;
        }

        public Iterator<BlockState> iterator() {
            return this.ids.iterator();
        }

        public void addMapping(BlockState state, int id) {
            this.ids.addMapping(state, id);
        }
    }
}
