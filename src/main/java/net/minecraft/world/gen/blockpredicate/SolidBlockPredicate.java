/*
 * Decompiled with CFR 0.1.1 (FabricMC 57d88659).
 */
package net.minecraft.world.gen.blockpredicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.gen.blockpredicate.BlockPredicateType;
import net.minecraft.world.gen.blockpredicate.OffsetPredicate;

public class SolidBlockPredicate
extends OffsetPredicate {
    public static final Codec<SolidBlockPredicate> CODEC = RecordCodecBuilder.create(instance -> SolidBlockPredicate.registerOffsetField(instance).apply(instance, SolidBlockPredicate::new));

    public SolidBlockPredicate(Vec3i arg) {
        super(arg);
    }

    @Override
    protected boolean test(BlockState state) {
        return state.getMaterial().isSolid();
    }

    @Override
    public BlockPredicateType<?> getType() {
        return BlockPredicateType.SOLID;
    }
}

