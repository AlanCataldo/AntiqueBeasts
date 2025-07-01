package net.mebahel.antiquebeasts.util;

import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

import java.util.EnumSet;

public class LargeEntityPathNodeMaker extends LandPathNodeMaker {

    @Override
    public PathNodeType findNearbyNodeTypes(BlockView world, int x, int y, int z, EnumSet<PathNodeType> nearbyTypes, PathNodeType type, BlockPos pos) {
        int xSize = Math.max(2, this.entityBlockXSize);
        int ySize = Math.max(4, this.entityBlockYSize);
        int zSize = Math.max(2, this.entityBlockZSize);

        for (int dx = 0; dx < xSize; ++dx) {
            for (int dy = 0; dy < ySize; ++dy) {
                for (int dz = 0; dz < zSize; ++dz) {
                    int px = x + dx;
                    int py = y + dy;
                    int pz = z + dz;
                    PathNodeType nodeType = this.getDefaultNodeType(world, px, py, pz);
                    nodeType = this.adjustNodeType(world, pos, nodeType);

                    if (dx == 0 && dy == 0 && dz == 0) {
                        type = nodeType;
                    }

                    nearbyTypes.add(nodeType);
                }
            }
        }
        return type;
    }
}
