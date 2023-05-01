/*
 * Decompiled with CFR 0.1.1 (FabricMC 57d88659).
 */
package net.minecraft.world.gen.structure;

import com.mojang.serialization.Codec;
import java.util.Optional;
import net.minecraft.structure.BuriedTreasureGenerator;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;

public class BuriedTreasureStructure
extends Structure {
    public static final Codec<BuriedTreasureStructure> CODEC = BuriedTreasureStructure.createCodec(BuriedTreasureStructure::new);

    public BuriedTreasureStructure(Structure.Config arg) {
        super(arg);
    }

    @Override
    public Optional<Structure.StructurePosition> getStructurePosition(Structure.Context context) {
        return BuriedTreasureStructure.getStructurePosition(context, Heightmap.Type.OCEAN_FLOOR_WG, collector -> BuriedTreasureStructure.addPieces(collector, context));
    }

    private static void addPieces(StructurePiecesCollector collector, Structure.Context context) {
        BlockPos lv = new BlockPos(context.chunkPos().getOffsetX(9), 90, context.chunkPos().getOffsetZ(9));
        collector.addPiece(new BuriedTreasureGenerator.Piece(lv));
    }

    @Override
    public StructureType<?> getType() {
        return StructureType.BURIED_TREASURE;
    }
}

