package net.mebahel.antiquebeasts.item.scroll;

import net.mebahel.antiquebeasts.entity.projectiles.FrostSpikeEntity;
import net.mebahel.antiquebeasts.entity.projectiles.HarpyFeatherEntity;
import net.mebahel.antiquebeasts.item.TickScheduler;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.mebahel.antiquebeasts.util.entity.ProjectileUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Objects;


public class ScrollItem extends Item {
    private final ProjectileUtil projectileUtil;
    private final int cooldown;
    private final float damage;
    private final String spell;
    private final TickScheduler tickScheduler;

    public ScrollItem(Settings settings, int cooldown, float damage, String spell, TickScheduler tickScheduler) {
        super(settings);
        this.cooldown = cooldown;
        this.damage = damage;
        this.spell = spell;
        this.projectileUtil = new ProjectileUtil();
        this.tickScheduler = tickScheduler;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);


        if (Objects.equals(spell, "ice_spike"))
            projectileUtil.scrollIceSpike(world, player,8f);
        else if (Objects.equals(spell, "frostbite")) {
            if (world instanceof ServerWorld serverWorld) {
                world.playSound(null, player.getX(), player.getY(), player.getZ(),
                        ModSounds.DRAUGR_FROST_SPELL, SoundCategory.NEUTRAL, 0.8F, 1.0F);
                int interval = 5;
                int numShots = 40 / interval;

                for (int i = 0; i <= numShots; i++) {
                    int delay = i * interval;

                    tickScheduler.schedule(serverWorld, delay, w -> {
                        projectileUtil.scrollFrostbite(world, player, 2.5f);
                    });
                }
            }
        }

        player.getItemCooldownManager().set(this, this.cooldown);
        player.incrementStat(Stats.USED.getOrCreateStat(this));
        return TypedActionResult.success(itemStack, world.isClient());
    }
}
