package org.dawnoftimevillage.world.buildingsite;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;

public class BuildingSiteUtils {

    public static BlockPos transformBlockPos(BlockPos posInStructure, Mirror mirror, Rotation rotation, BlockPos rotationPivot) {
        int i = posInStructure.getX();
        int j = posInStructure.getY();
        int k = posInStructure.getZ();
        boolean flag = true;
        switch (mirror) {
            case LEFT_RIGHT:
                k = -k;
                break;
            case FRONT_BACK:
                i = -i;
                break;
            default:
                flag = false;
        }

        int l = rotationPivot.getX();
        int i1 = rotationPivot.getZ();
        switch (rotation) {
            case COUNTERCLOCKWISE_90:
                return new BlockPos(l - i1 + k, j, l + i1 - i);
            case CLOCKWISE_90:
                return new BlockPos(l + i1 - k, j, i1 - l + i);
            case CLOCKWISE_180:
                return new BlockPos(l + l - i, j, i1 + i1 - k);
            default:
                return flag ? new BlockPos(i, j, k) : posInStructure;
        }
    }

    public static BlockState transformBlockState(BlockState state, Mirror mirror, Rotation rotation) {
        return state.mirror(mirror).rotate(rotation);
    }
}