/*
 * Decompiled with CFR 0.1.1 (FabricMC 57d88659).
 */
package net.minecraft.item;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.dynamic.Codecs;

public record Instrument(RegistryEntry<SoundEvent> soundEvent, int useDuration, float range) {
    public static final Codec<Instrument> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)SoundEvent.ENTRY_CODEC.fieldOf("sound_event")).forGetter(Instrument::soundEvent), ((MapCodec)Codecs.POSITIVE_INT.fieldOf("use_duration")).forGetter(Instrument::useDuration), ((MapCodec)Codecs.POSITIVE_FLOAT.fieldOf("range")).forGetter(Instrument::range)).apply((Applicative<Instrument, ?>)instance, Instrument::new));
}

