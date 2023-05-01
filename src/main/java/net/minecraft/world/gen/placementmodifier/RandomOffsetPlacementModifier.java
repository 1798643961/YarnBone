/*
 * Decompiled with CFR 0.1.1 (FabricMC 57d88659).
 */
package net.minecraft.world.gen.placementmodifier;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.feature.FeaturePlacementContext;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;
import net.minecraft.world.gen.placementmodifier.PlacementModifierType;

public class RandomOffsetPlacementModifier
extends PlacementModifier {
    public static final Codec<RandomOffsetPlacementModifier> MODIFIER_CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)IntProvider.createValidatingCodec(-16, 16).fieldOf("xz_spread")).forGetter(arg -> arg.spreadXz), ((MapCodec)IntProvider.createValidatingCodec(-16, 16).fieldOf("y_spread")).forGetter(arg -> arg.spreadY)).apply((Applicative<RandomOffsetPlacementModifier, ?>)instance, RandomOffsetPlacementModifier::new));
    private final IntProvider spreadXz;
    private final IntProvider spreadY;

    public static RandomOffsetPlacementModifier of(IntProvider spreadXz, IntProvider spreadY) {
        return new RandomOffsetPlacementModifier(spreadXz, spreadY);
    }

    public static RandomOffsetPlacementModifier vertically(IntProvider spreadY) {
        return new RandomOffsetPlacementModifier(ConstantIntProvider.create(0), spreadY);
    }

    public static RandomOffsetPlacementModifier horizontally(IntProvider spreadXz) {
        return new RandomOffsetPlacementModifier(spreadXz, ConstantIntProvider.create(0));
    }

    private RandomOffsetPlacementModifier(IntProvider xzSpread, IntProvider ySpread) {
        this.spreadXz = xzSpread;
        this.spreadY = ySpread;
    }

    @Override
    public Stream<BlockPos> getPositions(FeaturePlacementContext context, Random random, BlockPos pos) {
        int i = pos.getX() + this.spreadXz.get(random);
        int j = pos.getY() + this.spreadY.get(random);
        int k = pos.getZ() + this.spreadXz.get(random);
        return Stream.of(new BlockPos(i, j, k));
    }

    @Override
    public PlacementModifierType<?> getType() {
        return PlacementModifierType.RANDOM_OFFSET;
    }
}
