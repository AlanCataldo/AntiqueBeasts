package net.mebahel.antiquebeasts.item;

import net.mebahel.antiquebeasts.entity.custom.NorseEntity;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.GoatHornItem;
import net.minecraft.item.Instrument;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.stat.Stats;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class EinherjarHorn extends Item {

    private static final String INSTRUMENT_KEY = "instrument";
    private final TagKey<Instrument> instrumentTag;

    public EinherjarHorn(Item.Settings settings, TagKey<Instrument> instrumentTag) {
        super(settings);
        this.instrumentTag = instrumentTag;
    }

    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        Optional<RegistryKey<Instrument>> optional = this.getInstrument(stack).flatMap(RegistryEntry::getKey);
        if (optional.isPresent()) {
            tooltip.add(Text.translatable("item.antiquebeasts.einherjar_horn.tooltip").formatted(Formatting.GRAY));
        }

    }

    public static ItemStack getStackForInstrument(Item item, RegistryEntry<Instrument> instrument) {
        ItemStack itemStack = new ItemStack(item);
        setInstrument(itemStack, instrument);
        return itemStack;
    }

    public static void setRandomInstrumentFromTag(ItemStack stack, TagKey<Instrument> instrumentTag, Random random) {
        Optional<RegistryEntry<Instrument>> optional = Registries.INSTRUMENT.getEntryList(instrumentTag).flatMap((entryList) -> {
            return entryList.getRandom(random);
        });
        optional.ifPresent((instrument) -> {
            setInstrument(stack, instrument);
        });
    }

    private static void setInstrument(ItemStack stack, RegistryEntry<Instrument> instrument) {
        NbtCompound nbtCompound = stack.getOrCreateNbt();
        nbtCompound.putString("instrument", ((RegistryKey)instrument.getKey().orElseThrow(() -> {
            return new IllegalStateException("Invalid instrument");
        })).getValue().toString());
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        Optional<? extends RegistryEntry<Instrument>> optional = this.getInstrument(itemStack);
        if (optional.isPresent()) {
            List<TameableEntity> tamedAnimals = user.getWorld().getEntitiesByClass(TameableEntity.class, user.getBoundingBox().expand(20),
                    tameableEntity -> tameableEntity.isTamed() && tameableEntity.getOwner() == user);
            for (TameableEntity pet : tamedAnimals) {
                pet.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 1200, 0));
            }
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 1200, 0));
            Instrument instrument = (Instrument)((RegistryEntry)optional.get()).value();
            user.setCurrentHand(hand);
            playSound(world, user, instrument);
            user.getItemCooldownManager().set(this, instrument.useDuration());
            user.incrementStat(Stats.USED.getOrCreateStat(this));
            if (itemStack.isDamageable()) {
                itemStack.damage(1, user, (p) -> p.sendToolBreakStatus(hand));
            }
            return TypedActionResult.consume(itemStack);
        } else {
            return TypedActionResult.fail(itemStack);
        }
    }

    public int getMaxUseTime(ItemStack stack) {
        Optional<? extends RegistryEntry<Instrument>> optional = this.getInstrument(stack);
        return (Integer)optional.map((instrument) -> {
            return ((Instrument)instrument.value()).useDuration();
        }).orElse(0);
    }

    private Optional<? extends RegistryEntry<Instrument>> getInstrument(ItemStack stack) {
        NbtCompound nbtCompound = stack.getNbt();
        if (nbtCompound != null && nbtCompound.contains("instrument", 8)) {
            Identifier identifier = Identifier.tryParse(nbtCompound.getString("instrument"));
            if (identifier != null) {
                return Registries.INSTRUMENT.getEntry(RegistryKey.of(RegistryKeys.INSTRUMENT, identifier));
            }
        }

        Iterator<RegistryEntry<Instrument>> iterator = Registries.INSTRUMENT.iterateEntries(this.instrumentTag).iterator();
        return iterator.hasNext() ? Optional.of((RegistryEntry)iterator.next()) : Optional.empty();
    }

    public UseAction getUseAction(ItemStack stack) {
        return UseAction.TOOT_HORN;
    }

    private static void playSound(World world, PlayerEntity player, Instrument instrument) {
        float f = instrument.range() / 16.0F;
        world.playSoundFromEntity(player, player, ModSounds.EINHERJAR_HORN, SoundCategory.RECORDS, f, 1.0F);
        world.emitGameEvent(GameEvent.INSTRUMENT_PLAY, player.getPos(), GameEvent.Emitter.of(player));
    }
}
