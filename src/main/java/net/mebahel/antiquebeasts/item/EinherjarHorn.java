package net.mebahel.antiquebeasts.item;


import net.mebahel.antiquebeasts.item.custom.ModItems;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.stat.Stats;
import net.minecraft.tag.TagKey;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class EinherjarHorn extends Item {

    private static final String INSTRUMENT_KEY = "instrument";
    private TagKey<Instrument> instrumentTag;

    public EinherjarHorn(Item.Settings settings, TagKey<Instrument> instrumentTag) {
        super(settings);
        this.instrumentTag = instrumentTag;
    }

    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        Optional<RegistryKey<Instrument>> optional = this.getInstrument(stack).flatMap(RegistryEntry::getKey);
        if (optional.isPresent()) {
            MutableText mutableText = Text.translatable(Util.createTranslationKey("instrument", ((RegistryKey)optional.get()).getValue()));
            tooltip.add(mutableText.formatted(Formatting.GRAY));
        }

    }

    public static ItemStack getStackForInstrument(Item item, RegistryEntry<Instrument> instrument) {
        ItemStack itemStack = new ItemStack(item);
        setInstrument(itemStack, instrument);
        return itemStack;
    }

    public static void setRandomInstrumentFromTag(ItemStack stack, TagKey<Instrument> instrumentTag, Random random) {
        Optional<RegistryEntry<Instrument>> optional = Registry.INSTRUMENT.getEntryList(instrumentTag).flatMap((entryList) -> {
            return entryList.getRandom(random);
        });
        if (optional.isPresent()) {
            setInstrument(stack, (RegistryEntry)optional.get());
        }

    }

    private static void setInstrument(ItemStack stack, RegistryEntry<Instrument> instrument) {
        NbtCompound nbtCompound = stack.getOrCreateNbt();
        nbtCompound.putString("instrument", ((RegistryKey)instrument.getKey().orElseThrow(() -> {
            return new IllegalStateException("Invalid instrument");
        })).getValue().toString());
    }

    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
        if (this.isIn(group)) {
            Iterator var3 = Registry.INSTRUMENT.iterateEntries(this.instrumentTag).iterator();

            while(var3.hasNext()) {
                RegistryEntry<Instrument> registryEntry = (RegistryEntry)var3.next();
                stacks.add(getStackForInstrument(ModItems.EINHERJAR_HORN, registryEntry));
            }
        }
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        Optional<RegistryEntry<Instrument>> optional = this.getInstrument(itemStack);
        System.out.println("J'UTILISE :" + optional);
        if (optional.isPresent()) {
            Instrument instrument = (Instrument)((RegistryEntry)optional.get()).value();
            user.setCurrentHand(hand);
            System.out.println("JE JOUE");
            List<TameableEntity> tamedAnimals = user.getWorld().getEntitiesByClass(TameableEntity.class, user.getBoundingBox().expand(20),
                    tameableEntity -> tameableEntity.isTamed() && tameableEntity.getOwner() == user);
            for (TameableEntity pet : tamedAnimals) {
                pet.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 1200, 0));
            }
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 1200, 0));
            playSound(world, user, instrument);
            user.getItemCooldownManager().set(this, instrument.useDuration());
            return TypedActionResult.consume(itemStack);
        } else {
            return TypedActionResult.fail(itemStack);
        }
    }

    public int getMaxUseTime(ItemStack stack) {
        Optional<RegistryEntry<Instrument>> optional = this.getInstrument(stack);
        return optional.isPresent() ? ((Instrument)((RegistryEntry)optional.get()).value()).useDuration() : 0;
    }

    private Optional<RegistryEntry<Instrument>> getInstrument(ItemStack stack) {
        NbtCompound nbtCompound = stack.getNbt();
        if (nbtCompound != null) {
            Identifier identifier = Identifier.tryParse(nbtCompound.getString("instrument"));
            System.out.println("IDENTIFIER :" + nbtCompound);
            System.out.println("IDENTIFIER :" + identifier);
            if (identifier != null) {
                return Registry.INSTRUMENT.getEntry(RegistryKey.of(Registry.INSTRUMENT_KEY, identifier));
            }
        }

        Iterator<RegistryEntry<Instrument>> iterator = Registry.INSTRUMENT.iterateEntries(this.instrumentTag).iterator();
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
