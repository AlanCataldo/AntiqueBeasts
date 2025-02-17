package net.mebahel.antiquebeasts.block.custom;

import net.mebahel.antiquebeasts.entity.ModEntities;
import net.mebahel.antiquebeasts.entity.custom.egyptian.MummyBossEntity;
import net.mebahel.antiquebeasts.item.custom.ModItems;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class MummyBossAltarBlock extends HorizontalFacingBlock {

    // Propriété pour vérifier si le boss a déjà été invoqué
    public static final BooleanProperty BOSS_SUMMONED = BooleanProperty.of("boss_summoned");

    public MummyBossAltarBlock(Settings settings) {
        super(settings.nonOpaque());
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(BOSS_SUMMONED, false)
                .with(FACING, Direction.NORTH)); // Orientation par défaut
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(BOSS_SUMMONED, FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        // Obtenir la direction selon le joueur qui place le bloc
        return this.getDefaultState().with(FACING, context.getHorizontalPlayerFacing().getOpposite());
    }

    // Méthode déclenchée lors de l'interaction avec le bloc
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            ServerWorld serverWorld = (ServerWorld) world;

            // Vérifier si le boss a déjà été invoqué
            if (!state.get(BOSS_SUMMONED)) {
                ItemStack heldItem = player.getStackInHand(hand);

                // Vérifier si le joueur tient un ANKH
                if (heldItem.getItem() == ModItems.ANKH) {
                    // Consommer l'ANKH
                    if (!player.isCreative()) {
                        heldItem.decrement(1);
                    }

                    // Invoquer le boss derrière l'autel
                    Direction facing = state.get(FACING);
                    summonMummyBoss(serverWorld, pos, facing);

                    // Jouer un son d'invocation
                    serverWorld.playSound(null, pos, ModSounds.MUMMY_BOSS_SECOND_PHASE, SoundCategory.BLOCKS, 1.0F, 1.0F);

                    // Casser le bloc après l'invocation
                    world.breakBlock(pos, false); // `false` pour éviter de faire tomber des objets

                    return ActionResult.SUCCESS;
                }
            } else {
                player.sendMessage(Text.of("Le boss a déjà été invoqué !"), true);
                return ActionResult.PASS;
            }
        }
        return ActionResult.PASS;
    }

    private void summonMummyBoss(ServerWorld world, BlockPos altarPos, Direction altarFacing) {
        // Calculer la position derrière l'autel (7 blocs derrière)
        BlockPos spawnPos = altarPos.offset(altarFacing.getOpposite(), 7);
        Vec3d spawnVec = new Vec3d(spawnPos.getX() + 0.5, spawnPos.getY() + 3, spawnPos.getZ() + 0.5);

        // Créer l'entité MummyBoss
        MummyBossEntity mummyBoss = ModEntities.MUMMY_BOSS.create(world);
        if (mummyBoss != null) {
            // Rotation pour que la MummyBoss regarde l'autel
            float yaw = altarFacing.asRotation();

            mummyBoss.initialize(world, world.getLocalDifficulty(spawnPos), SpawnReason.EVENT, null, null);
            mummyBoss.refreshPositionAndAngles(spawnVec.x, spawnVec.y, spawnVec.z, yaw, 0);
            mummyBoss.setBodyYaw(yaw);
            mummyBoss.setHeadYaw(yaw);

            world.spawnEntity(mummyBoss);
        }
    }

    // Appliquer un effet de Mining Fatigue aux joueurs proches
    public void applyEffectToNearbyPlayers(ServerWorld world, BlockPos pos) {
        Box effectArea = new Box(pos).expand(150);  // Rayon de 150 blocs
        for (PlayerEntity player : world.getEntitiesByClass(PlayerEntity.class, effectArea, playerEntity -> true)) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 600, 2, false, false, false));
        }
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!world.isClient) {
            // Planifier le tick initial dès que le bloc est ajouté, qu'il soit placé manuellement ou généré automatiquement.
            scheduleNextEffectTick((ServerWorld) world, pos);
        }
        super.onBlockAdded(state, world, pos, oldState, notify);
    }

    // Planifier un tick toutes les 15 secondes
    public void scheduleNextEffectTick(ServerWorld world, BlockPos pos) {
        world.scheduleBlockTick(pos, this, 100);  // 100 ticks = 5 secondes
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, net.minecraft.util.math.random.Random random) {
        // Appliquer l'effet de Mining Fatigue aux joueurs proches
        applyEffectToNearbyPlayers(world, pos);

        // Re-planifier un autre tick pour que l'effet continue de s'appliquer
        scheduleNextEffectTick(world, pos);
    }
}
