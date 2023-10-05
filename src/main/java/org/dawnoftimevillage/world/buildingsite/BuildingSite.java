package org.dawnoftimevillage.world.buildingsite;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Clearable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.dawnoftimevillage.util.DoTVUtils;

import java.util.Optional;

public class BuildingSite {
    private final ServerLevel level;
    private String name;
    private final BuildingPlan buildingPlan;
    private int nextBlockToBuildIndex; // AKA construction progress
    private boolean completed;
    private BuildingSiteSettings settings;

    BuildingSite(ServerLevel level, String name, ResourceLocation structureNbtFile, BuildingSiteSettings settings) {
        this.level = level;
        this.name = name;
        this.buildingPlan = BuildingPlan.createFromResource(structureNbtFile, this.level.getServer().getResourceManager());
        this.nextBlockToBuildIndex = 0;
        this.settings = settings;
    }

    BuildingSite(ServerLevel level, CompoundTag compoundTag) {
        this.level = level;
        this.name = compoundTag.getString("Name");
        this.buildingPlan = BuildingPlan.createFromResource(DoTVUtils.resource(compoundTag.getString("Structure")), level.getServer().getResourceManager());
        this.nextBlockToBuildIndex = compoundTag.getInt("Progression");
        BlockPos buildPos = new BlockPos(compoundTag.getInt("BuildX"), compoundTag.getInt("BuildY"), compoundTag.getInt("BuildZ"));
        BuildingSiteSettings settings = new BuildingSiteSettings(buildPos)
                .rotation(Rotation.valueOf(compoundTag.getString("Rotation").toUpperCase()))
                .mirror(Mirror.valueOf(compoundTag.getString("Mirror").toUpperCase()));
        this.settings = settings;
    }

    public CompoundTag save(CompoundTag tag) {
        tag.putString("Name", this.name);
        tag.putString("Structure", this.buildingPlan.getStructure().getPath());
        tag.putInt("Progression", this.nextBlockToBuildIndex);
        tag.putInt("BuildX", this.settings.getPosition().getX());
        tag.putInt("BuildY", this.settings.getPosition().getY());
        tag.putInt("BuildZ", this.settings.getPosition().getZ());
        tag.putString("Rotation", this.settings.getRotation().getSerializedName());
        tag.putString("Mirror", this.settings.getMirror().getSerializedName());
        return tag;
    }

    public boolean buildNextBlock() {
        return buildBlock(this.nextBlockToBuildIndex);
    }

    private boolean buildBlock(int index) {
        if (!this.completed && index < this.buildingPlan.getBlocksQuantity()) {
            BuildingSitesManager.get(level).setDirty();
            BlockPos pos = this.buildingPlan.getBlockPos(index);
            BlockState state = this.buildingPlan.getBlockState(index);
            CompoundTag nbt = this.buildingPlan.getBlockNBT(index);

            pos = ConstructionUtils.transformBlockPos(pos, this.settings.getMirror(), this.settings.getRotation(), this.settings.getRotationPivot()).offset(this.settings.getPosition());
            state = ConstructionUtils.transformBlockState(state, this.settings.getMirror(), this.settings.getRotation());

            if (nbt != null) {
                BlockEntity blockentity = this.level.getBlockEntity(pos);
                Clearable.tryClear(blockentity);
                this.level.setBlock(pos, Blocks.BARRIER.defaultBlockState(), 20);
            }

            boolean placed = this.level.setBlock(pos, state, 2);

            if (placed) {
                if (nbt != null) {
                    BlockEntity blockEntity = this.level.getBlockEntity(pos);
                    if (blockEntity != null) {
                        if (blockEntity instanceof RandomizableContainerBlockEntity) {
                            nbt.putLong("LootTableSeed", RandomSource.create().nextLong());
                        }

                        blockEntity.load(nbt);
                    }
                }

            }
            if (!isLastBlockToBuild(index)) {
                ++this.nextBlockToBuildIndex;
            } else {
                placeEntities();
                this.completed = true;
                BuildingSitesManager.get(level).notifyBuildingSiteCompleted(this);
            }
            return placed;
        }
        return false;
    }

    private void placeEntities() {
        for(BuildingPlan.EntityInfo entityInfo : this.buildingPlan.getEntities()) {
            Vec3 entityPos = ConstructionUtils.transformEntityPos(entityInfo.pos(), this.settings.getMirror(), this.settings.getRotation(), this.settings.getRotationPivot()).add(Vec3.atLowerCornerOf(this.settings.getPosition()));
            //BlockPos blockPos = ConstructionUtils.transformBlockPos(entityInfo.blockPos(), this.settings.getMirror(), this.settings.getRotation(), this.settings.getRotationPivot()).offset(this.settings.getPosition());
            CompoundTag nbt = entityInfo.nbt().copy();
            ListTag entityPosTag = new ListTag();
            entityPosTag.add(DoubleTag.valueOf(entityPos.x));
            entityPosTag.add(DoubleTag.valueOf(entityPos.y));
            entityPosTag.add(DoubleTag.valueOf(entityPos.z));
            nbt.put("Pos", entityPosTag);
            nbt.remove("UUID");
            createEntityIgnoreException(level, nbt).ifPresent((entity) -> {
                float f = entity.rotate(this.settings.getRotation());
                f += entity.mirror(this.settings.getMirror()) - entity.getYRot();
                entity.moveTo(entityPos.x, entityPos.y, entityPos.z, f, entity.getXRot());
                if (entity instanceof Mob mob) {
                    mob.finalizeSpawn(level, level.getCurrentDifficultyAt(BlockPos.containing(entityPos)), MobSpawnType.STRUCTURE,null, nbt);
                }
                level.addFreshEntityWithPassengers(entity);
            });
        }
    }

    private static Optional<Entity> createEntityIgnoreException(ServerLevelAccessor level, CompoundTag tag) {
        try {
            return EntityType.create(tag, level.getLevel());
        } catch (Exception exception) {
            return Optional.empty();
        }
    }

    public Block nextBlock() {
        return this.buildingPlan.getBlock(this.nextBlockToBuildIndex);
    }

    public BlockState nextBlockState() {
        return ConstructionUtils.transformBlockState(this.buildingPlan.getBlockState(this.nextBlockToBuildIndex), this.settings.getMirror(), this.settings.getRotation());
    }

    public BlockPos nextBlockPos() {
        return ConstructionUtils.transformBlockPos(this.buildingPlan.getBlockPos(this.nextBlockToBuildIndex), this.settings.getMirror(), this.settings.getRotation(), this.settings.getRotationPivot()).offset(this.settings.getPosition());
    }

    public Block lastBlockBuilt() {
        return this.buildingPlan.getBlock(this.nextBlockToBuildIndex - 1);
    }

    public BlockPos lastBlockPos() {
        return ConstructionUtils.transformBlockPos(this.buildingPlan.getBlockPos(this.nextBlockToBuildIndex - 1), this.settings.getMirror(), this.settings.getRotation(), this.settings.getRotationPivot()).offset(this.settings.getPosition());
    }

    public BlockState lastBlockState() {
        return ConstructionUtils.transformBlockState(this.buildingPlan.getBlockState(this.nextBlockToBuildIndex - 1), this.settings.getMirror(), this.settings.getRotation());
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

    public int getNextBlockToBuildIndex() {return this.nextBlockToBuildIndex;}

    public boolean isCompleted() {return this.completed;}

    public BuildingPlan getBuildingPlan() {return this.buildingPlan;}

    public String getName() {return this.name;}

    public void setNextBlockToBuildIndex(int nextBlockToBuildIndex) {this.nextBlockToBuildIndex = nextBlockToBuildIndex;}
}
