package org.dawnoftimevillage.world.buildingsite;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;

public class BuildingSiteSettings {
    private BlockPos position;
    private Mirror mirror = Mirror.NONE;
    private Rotation rotation = Rotation.NONE;
    private BlockPos rotationPivot = BlockPos.ZERO;

    public BuildingSiteSettings(BlockPos position) {
        this.position = position;
    }

    public static BuildingSiteSettings create(BlockPos position) {
        return new BuildingSiteSettings(position);
    }

    public BuildingSiteSettings mirror(Mirror mirror) {
        this.mirror = mirror;
        return this;
    }

    public BuildingSiteSettings rotation(Rotation rotation) {
        this.rotation = rotation;
        return this;
    }

    public BuildingSiteSettings rotationPivot(BlockPos rotationPivot) {
        this.rotationPivot = rotationPivot;
        return this;
    }

    public BlockPos getPosition() {return this.position;}

    public Mirror getMirror() {return this.mirror;}

    public Rotation getRotation() {return this.rotation;}

    public BlockPos getRotationPivot() {return this.rotationPivot;}
}
