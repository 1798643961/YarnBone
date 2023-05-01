/*
 * Decompiled with CFR 0.1.1 (FabricMC 57d88659).
 */
package net.minecraft.util.math;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class GlobalPos {
    public static final Codec<GlobalPos> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)World.CODEC.fieldOf("dimension")).forGetter(GlobalPos::getDimension), ((MapCodec)BlockPos.CODEC.fieldOf("pos")).forGetter(GlobalPos::getPos)).apply((Applicative<GlobalPos, ?>)instance, GlobalPos::create));
    private final RegistryKey<World> dimension;
    private final BlockPos pos;

    private GlobalPos(RegistryKey<World> dimension, BlockPos pos) {
        this.dimension = dimension;
        this.pos = pos;
    }

    public static GlobalPos create(RegistryKey<World> dimension, BlockPos pos) {
        return new GlobalPos(dimension, pos);
    }

    public RegistryKey<World> getDimension() {
        return this.dimension;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        GlobalPos lv = (GlobalPos)o;
        return Objects.equals(this.dimension, lv.dimension) && Objects.equals(this.pos, lv.pos);
    }

    public int hashCode() {
        return Objects.hash(this.dimension, this.pos);
    }

    public String toString() {
        return this.dimension + " " + this.pos;
    }
}

