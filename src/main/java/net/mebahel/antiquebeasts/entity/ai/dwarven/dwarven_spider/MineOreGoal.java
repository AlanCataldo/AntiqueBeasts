package net.mebahel.antiquebeasts.entity.ai.dwarven.dwarven_spider;

import net.mebahel.antiquebeasts.entity.custom.dwarven.DwarvenSpiderEntity;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;

public class MineOreGoal extends Goal {
    private final DwarvenSpiderEntity spider;
    private BlockPos targetOre = null;
    private int miningTime = 0;
    private boolean miningFished = false;
    private static final int MINING_DURATION = 9 * 20; // 10s en ticks
    private static final int MINING_COOLDOWN = 40 * 20; // 40s en ticks
    private static final List<Block> ORES = Arrays.asList(
            Blocks.IRON_ORE, Blocks.GOLD_ORE, Blocks.DIAMOND_ORE, Blocks.EMERALD_ORE,
            Blocks.LAPIS_ORE, Blocks.REDSTONE_ORE, Blocks.DEEPSLATE_IRON_ORE, Blocks.DEEPSLATE_GOLD_ORE,
            Blocks.DEEPSLATE_DIAMOND_ORE, Blocks.DEEPSLATE_EMERALD_ORE, Blocks.DEEPSLATE_LAPIS_ORE,
            Blocks.DEEPSLATE_REDSTONE_ORE, Blocks.COPPER_ORE, Blocks.DEEPSLATE_COPPER_ORE,
            Blocks.NETHER_QUARTZ_ORE, Blocks.NETHER_GOLD_ORE, Blocks.COAL_ORE, Blocks.DEEPSLATE_COAL_ORE
    );

    public MineOreGoal(DwarvenSpiderEntity spider) {
        this.spider = spider;
    }

    @Override
    public boolean canStart() {
        if (spider.getDataTracker().get(DwarvenSpiderEntity.IS_MINING) ||
                spider.getDataTracker().get(DwarvenSpiderEntity.MINING_COOLDOWN) > 0) {
            return false;
        }
        if (spider.getTarget() != null) {
            return false;
        }
        targetOre = findNearbyOre();
        return targetOre != null;
    }

    @Override
    public void start() {
        if (targetOre != null) {
            BlockPos bestPosition = findBestMiningPosition(targetOre);
            System.out.println("🚶 Dwarven Spider se dirige vers " + bestPosition);
            spider.getNavigation().startMovingTo(bestPosition.getX(), bestPosition.getY(), bestPosition.getZ(), 1.0);
            miningTime = 0;
        }
    }

    @Override
    public boolean shouldContinue() {
        return miningTime <= MINING_DURATION && this.spider.getTarget() == null && !this.miningFished;
    }

    @Override
    public void tick() {
        if (targetOre == null || !isStillOre(targetOre)) {
            System.out.println("❌ Bloc cible manquant ou remplacé, arrêt du minage !");
            miningFished = true;
            return;
        }

        BlockPos bestPosition = findBestMiningPosition(targetOre);
        double distance = spider.getPos().distanceTo(bestPosition.toCenterPos());

        // **Forcer l'araignée à finir son chemin avant de miner**
        if (!spider.getNavigation().isIdle()) {
            System.out.println("📍 L'araignée est en mouvement, elle ne commence pas encore à miner.");
            return;
        }

        if (distance < 1.5) { // Vérifier si l'araignée est bien positionnée
            spider.getLookControl().lookAt(targetOre.getX() + 0.5, targetOre.getY() + 0.5, targetOre.getZ() + 0.5);

            if (!spider.getDataTracker().get(DwarvenSpiderEntity.IS_MINING)) {
                System.out.println("⛏ Dwarven Spider commence à miner !");
                spider.getDataTracker().set(DwarvenSpiderEntity.IS_MINING, true);
                miningTime = 0;
            }

            miningTime++;
            System.out.println("⏳ Minage en cours... " + miningTime + "/" + MINING_DURATION);

            if (miningTime % 7 == 0) {
                spawnMiningParticles(targetOre);
                spider.getWorld().playSound(null, targetOre, net.minecraft.sound.SoundEvents.BLOCK_STONE_BREAK,
                        net.minecraft.sound.SoundCategory.BLOCKS, 0.5f, 1.0f);
            }

            int breakProgress = (int) ((miningTime / (float) MINING_DURATION) * 10);
            spider.getWorld().setBlockBreakingInfo(spider.getId(), targetOre, breakProgress);

            if (miningTime >= MINING_DURATION) {
                System.out.println("✅ Dwarven Spider a terminé de miner !");
                spider.collectDroppedItems(targetOre);
                spider.getWorld().breakBlock(targetOre, false);
                miningFished = true;
            }
        }
    }

    /**
     * Vérifie si le bloc à la position targetOre est toujours un minerai.
     */
    private boolean isStillOre(BlockPos pos) {
        World world = spider.getWorld();
        Block block = world.getBlockState(pos).getBlock();
        return ORES.contains(block);
    }


    @Override
    public void stop() {
        System.out.println("🛑 Dwarven Spider arrête de miner et entre en cooldown de 40s.");
        spider.getDataTracker().set(DwarvenSpiderEntity.IS_MINING, false);
        spider.getDataTracker().set(DwarvenSpiderEntity.MINING_COOLDOWN, MINING_COOLDOWN);
        this.miningFished = false;
        targetOre = null;
        miningTime = 0;
    }

    private BlockPos findNearbyOre() {
        BlockPos spiderPos = spider.getBlockPos();
        World world = spider.getWorld();

        for (BlockPos pos : BlockPos.iterateOutwards(spiderPos, 10, 3, 10)) {
            if (isOreExposed(world, pos)) {
                return pos;
            }
        }
        return null;
    }

    private boolean isOreExposed(World world, BlockPos pos) {
        Block block = world.getBlockState(pos).getBlock();
        if (!ORES.contains(block)) return false;

        for (BlockPos neighbor : new BlockPos[]{pos.up(), pos.down(), pos.north(), pos.south(), pos.east(), pos.west()}) {
            if (world.getBlockState(neighbor).isAir()) return true;
        }
        return false;
    }

    private void spawnMiningParticles(BlockPos pos) {
        World world = spider.getWorld();
        if (world.isClient) return;

        net.minecraft.block.BlockState blockState = world.getBlockState(pos);

        for (int i = 0; i < 5; i++) {
            double offsetX = (Math.random() - 0.5) * 0.6;
            double offsetY = (Math.random() - 0.5) * 0.6;
            double offsetZ = (Math.random() - 0.5) * 0.6;

            ((net.minecraft.server.world.ServerWorld) world).spawnParticles(
                    new net.minecraft.particle.BlockStateParticleEffect(net.minecraft.particle.ParticleTypes.BLOCK, blockState),
                    pos.getX() + 0.5 + offsetX,
                    pos.getY() + 0.5 + offsetY,
                    pos.getZ() + 0.5 + offsetZ,
                    5, 0.0, 0.0, 0.0, 0.1
            );
        }
    }
    private BlockPos findBestMiningPosition(BlockPos orePos) {
        World world = spider.getWorld();
        BlockPos bestPos = null;
        double bestDistance = Double.MAX_VALUE;

        // Essayer toutes les positions autour du minerai
        for (BlockPos neighbor : new BlockPos[]{
                orePos.north(), orePos.south(), orePos.east(), orePos.west(),
                orePos.north().up(), orePos.south().up(), orePos.east().up(), orePos.west().up(),
                orePos.north().down(), orePos.south().down(), orePos.east().down(), orePos.west().down()
        }) {
            // Vérifier si le bloc adjacent est solide pour se tenir dessus ET si l'araignée peut marcher dessus
            if (world.getBlockState(neighbor.down()).isSolidBlock(world, neighbor.down()) &&
                    world.getBlockState(neighbor).isAir()) {

                double distance = neighbor.getSquaredDistance(spider.getPos());

                // Garde la position la plus proche et accessible
                if (distance < bestDistance) {
                    bestPos = neighbor;
                    bestDistance = distance;
                }
            }
        }

        return bestPos != null ? bestPos : orePos.up(); // Par défaut, elle monte au-dessus du minerai
    }


}
