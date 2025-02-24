package net.mebahel.antiquebeasts.item.staff;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.mebahel.antiquebeasts.item.custom.ModItems;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.mebahel.antiquebeasts.util.entity.ProjectileUtil;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TridentItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FrostBiteStaff extends TridentItem {
    private final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;
    private final ProjectileUtil projectileUtil = new ProjectileUtil();

    public FrostBiteStaff(Item.Settings settings) {
        super(settings);
        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Tool modifier", 4.0, EntityAttributeModifier.Operation.ADDITION));
        builder.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Tool modifier", -2.9000000953674316, EntityAttributeModifier.Operation.ADDITION));
        this.attributeModifiers = builder.build();
    }

    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        return !miner.isCreative();
    }

    public UseAction getUseAction(ItemStack stack) {
        return UseAction.NONE;
    }

    public int getMaxUseTime(ItemStack stack) {
        return 72000;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        user.setCurrentHand(hand);

        NbtCompound nbt = itemStack.getOrCreateNbt();

        // ✅ Jouer immédiatement le son FROST_SPELL_3 si ce n'est pas déjà fait
        if (world.isClient && !nbt.getBoolean("HasPlayedStartSound")) {
            user.playSound(ModSounds.FROST_SPELL_3, SoundCategory.PLAYERS, 1.0F, 1.0F);
            nbt.putBoolean("HasPlayedStartSound", true);
        }

        return TypedActionResult.consume(itemStack);
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity player)) return;
        int chargeTicks = this.getMaxUseTime(stack) - remainingUseTicks;
        Hand hand = player.getActiveHand();

        // ✅ Arrêter automatiquement après 5 secondes (100 ticks)
        if (chargeTicks >= 76) {
            player.stopUsingItem();
            return;
        }

        // ✅ Tirer seulement une fois toutes les 5 ticks (0.25s)
        if (chargeTicks % 5 == 0) {
            if (!world.isClient) {
                projectileUtil.scrollFrostbite(world, user, hand, 3f);
            }
        }
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity player)) return;
        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.putBoolean("HasPlayedStartSound", false);

        if (world.isClient) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.getSoundManager() != null) {
                client.getSoundManager().stopSounds(ModSounds.FROST_SPELL_3.getId(), SoundCategory.PLAYERS);
            }
        }
        stack.damage(1, player, (p) -> {
            p.sendToolBreakStatus(player.getActiveHand());
        });
        player.getItemCooldownManager().set(stack.getItem(), 40);
    }

    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.damage(1, attacker, (e) -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
        return true;
    }
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND ? this.attributeModifiers : super.getAttributeModifiers(slot);
    }
    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return ingredient.isOf(ModItems.FROST_SHARD);
    }

}
