package net.mebahel.antiquebeasts.item;

import net.mebahel.antiquebeasts.entity.projectiles.ThrowingAxeEntity;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class ThrowingAxeItem extends AxeItem {
    public ThrowingAxeItem(ToolMaterial material, float attackDamage, float attackSpeed, Settings settings) {
        super(material, attackDamage, attackSpeed, settings);
    }
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        world.playSound(null, user.getX(), user.getY(), user.getZ(), ModSounds.SWING, SoundCategory.NEUTRAL, 0.5F, 1.1F);

        if (!world.isClient) {
            ThrowingAxeEntity throwingAxeEntity = new ThrowingAxeEntity(world, user, 8);
            throwingAxeEntity.setItem(itemStack);
            throwingAxeEntity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, 0.85F, 0F);
            world.spawnEntity(throwingAxeEntity);
            user.getItemCooldownManager().set(this, 60);
            itemStack.damage(2, user, (p) -> p.sendToolBreakStatus(hand));
        }

        user.incrementStat(Stats.USED.getOrCreateStat(this));
        return TypedActionResult.success(itemStack, world.isClient());
    }
}
