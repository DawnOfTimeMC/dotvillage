package org.dawnoftimevillage.world.buildingsite;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Clearable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.dawnoftimevillage.util.DoTVUtils;

public class BuildingSite {
    private final ServerLevel level;
    private String name;
    private final BuildingPlan buildingPlan;
    private int progression;
    private boolean completed;
    private BuildingSiteSettings settings;

    BuildingSite(ServerLevel level, String name, ResourceLocation structureNbtFile, BuildingSiteSettings settings) {
        this.level = level;
        this.name = name;
        this.buildingPlan = BuildingPlan.createFromResource(structureNbtFile, this.level.getServer().getResourceManager());
        if (this.buildingPlan == null) {
            System.out.println("FAILED CONSTRUCTING PLAN");
        } else {
            System.out.println("SUCCESS CONSTRUCTING PLAN");
        }
        this.progression = 0;
        this.settings = settings;
    }

    BuildingSite(ServerLevel level, CompoundTag compoundTag) {
        this.level = level;
        this.name = compoundTag.getString("Name");
        this.buildingPlan = BuildingPlan.createFromResource(DoTVUtils.resource(compoundTag.getString("Structure")), level.getServer().getResourceManager());
        if (this.buildingPlan == null) {
            System.out.println("NBT : FAILED CONSTRUCTING PLAN");
        } else {
            System.out.println("NBT : SUCCESS CONSTRUCTING PLAN");
        }
        this.progression = compoundTag.getInt("Progression");
        BlockPos buildPos = new BlockPos(compoundTag.getInt("BuildX"), compoundTag.getInt("BuildY"), compoundTag.getInt("BuildZ"));
        BuildingSiteSettings settings = new BuildingSiteSettings(buildPos)
                .rotation(Rotation.valueOf(compoundTag.getString("Rotation").toUpperCase()))
                .mirror(Mirror.valueOf(compoundTag.getString("Mirror").toUpperCase()));
        this.settings = settings;
    }

    public CompoundTag save(CompoundTag tag) {
        tag.putString("Name", this.name);
        tag.putString("Structure", this.buildingPlan.getStructure().getPath());
        tag.putInt("Progression", this.progression);
        tag.putInt("BuildX", this.settings.getPosition().getX());
        tag.putInt("BuildY", this.settings.getPosition().getY());
        tag.putInt("BuildZ", this.settings.getPosition().getZ());
        tag.putString("Rotation", this.settings.getRotation().getSerializedName());
        tag.putString("Mirror", this.settings.getMirror().getSerializedName());
        return tag;
    }

    public boolean buildNextBlock() {
        return buildBlock(this.progression);
    }

    private boolean buildBlock(int index) {
        if (!this.completed && index < this.buildingPlan.getBlocksQuantity()) {
            BlockPos pos = this.buildingPlan.getBlockPos(index);
            BlockState state = this.buildingPlan.getBlockState(index);
            CompoundTag tag = this.buildingPlan.getBlockNBT(index);

            pos = BuildingSiteUtils.transformBlockPos(pos, this.settings.getMirror(), this.settings.getRotation(), this.settings.getRotationPivot()).offset(this.settings.getPosition());
            state = BuildingSiteUtils.transformBlockState(state, this.settings.getMirror(), this.settings.getRotation());

            if (tag != null) {
                BlockEntity blockentity = this.level.getBlockEntity(pos);
                Clearable.tryClear(blockentity);
                this.level.setBlock(pos, Blocks.BARRIER.defaultBlockState(), 20);
            }

            boolean placed = this.level.setBlock(pos, state, 2);

            if (placed) {
                if (tag != null) {
                    BlockEntity blockEntity = this.level.getBlockEntity(pos);
                    if (blockEntity != null) {
                        if (blockEntity instanceof RandomizableContainerBlockEntity) {
                            tag.putLong("LootTableSeed", RandomSource.create().nextLong());
                        }

                        blockEntity.load(tag);
                    }
                }

            }
            if (!isLastBlockToBuild(index)) {
                ++this.progression;
            } else {
                this.completed = true;
            }
            return placed;
        }
        return false;
    }

    public Block nextBlock() {
        return this.buildingPlan.getBlock(this.progression);
    }

    public BlockState nextBlockState() {
        return BuildingSiteUtils.transformBlockState(this.buildingPlan.getBlockState(this.progression), this.settings.getMirror(), this.settings.getRotation());
    }

    public BlockPos nextBlockPos() {
        return BuildingSiteUtils.transformBlockPos(this.buildingPlan.getBlockPos(this.progression), this.settings.getMirror(), this.settings.getRotation(), this.settings.getRotationPivot()).offset(this.settings.getPosition());
    }

    public Block lastBlockBuilt() {
        return this.buildingPlan.getBlock(this.progression - 1);
    }

    public BlockPos lastBlockPos() {
        return BuildingSiteUtils.transformBlockPos(this.buildingPlan.getBlockPos(this.progression - 1), this.settings.getMirror(), this.settings.getRotation(), this.settings.getRotationPivot()).offset(this.settings.getPosition());
    }

    public BlockState lastBlockState() {
        return BuildingSiteUtils.transformBlockState(this.buildingPlan.getBlockState(this.progression - 1), this.settings.getMirror(), this.settings.getRotation());
    }

    private boolean isLastBlockToBuild(int pIndex) {
        return ((pIndex + 1) == this.buildingPlan.getBlocksQuantity());
    }

    public int getBlocksQuantity() {
        return this.buildingPlan.getBlocksQuantity();
    }

    public BlockPos getLocation() {
        return this.settings.getPosition();
    }

    public int getProgression() {return this.progression;}

    public boolean isCompleted() {return this.completed;}

    public BuildingPlan getBuildingPlan() {return this.buildingPlan;}

    public String getName() {return this.name;}

    public void setProgression(int progression) {this.progression = progression;}
}
