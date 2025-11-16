package net.mebahel.antiquebeasts.item;

import net.mebahel.antiquebeasts.entity.projectiles.PharaohScepterProjectileEntity;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.util.ClientUtils;

import java.util.List;

public class PharaohScepterItem extends SwordItem {
    private static final int COOLDOWN_TICKS = 60; // 1 second of cooldown (20 ticks per second)

    private final TickScheduler tickScheduler;

    public PharaohScepterItem(TickScheduler tickScheduler, ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Item.Settings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
        this.tickScheduler = tickScheduler;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);

        if (!player.getItemCooldownManager().isCoolingDown(this)) {
            // Immediate projectile shot
            shootMummyProjectiles(world, player);

            // Schedule the second and third shots with delays
            if (world instanceof ServerWorld serverWorld) {
                tickScheduler.schedule(serverWorld, 15, w -> {
                    shootMummyProjectiles(w, player);
                });

                tickScheduler.schedule(serverWorld, 30, w -> {
                    shootMummyProjectiles(w, player);
                });
            }

            // Set cooldown for the item
            player.getItemCooldownManager().set(this, COOLDOWN_TICKS);
            player.playSound(SoundEvents.ITEM_TRIDENT_THROW, 1.0F, 1.0F);
            itemStack.damage(1, player, (p) -> p.sendToolBreakStatus(hand));
        }

        return TypedActionResult.success(itemStack);
    }

    private void shootMummyProjectiles(World world, PlayerEntity player) {
        Vec3d lookDirection = player.getRotationVec(1.0F).normalize();
        PharaohScepterProjectileEntity projectile = new PharaohScepterProjectileEntity(world, player, 8.0f);

        Vec3d playerPos = player.getPos();
        Vec3d spawnPosition = playerPos.add(lookDirection.multiply(1.0)).add(0, 1.5, 0);

        projectile.setPos(spawnPosition.x, spawnPosition.y, spawnPosition.z);
        projectile.setVelocity(lookDirection.multiply(0.5));
        projectile.setNoGravity(true);
        projectile.setOwner(player);
        if (world.isClient) {
            player = ClientUtils.getClientPlayer();
            if (player != null) {
                world.playSound(player, player.getX(), player.getY(), player.getZ(),
                        ModSounds.DARK_MAGIC_1, player.getSoundCategory(), 0.85f, 1f);
            }
        }

        world.spawnEntity(projectile);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.antiquebeasts.pharaoh_scepter_staff.tooltip").formatted(Formatting.GRAY, Formatting.ITALIC));
        tooltip.add(Text.translatable("item.antiquebeasts.pharaoh_scepter_staff.tooltip2").formatted(Formatting.GRAY, Formatting.ITALIC));
    }
}
