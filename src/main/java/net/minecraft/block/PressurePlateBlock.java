/*
 * Decompiled with CFR 0.1.1 (FabricMC 57d88659).
 */
package net.minecraft.block;

import java.util.List;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractPressurePlateBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSetType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class PressurePlateBlock
extends AbstractPressurePlateBlock {
    public static final BooleanProperty POWERED = Properties.POWERED;
    private final ActivationRule type;

    protected PressurePlateBlock(ActivationRule type, AbstractBlock.Settings settings, BlockSetType blockSetType) {
        super(settings, blockSetType);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(POWERED, false));
        this.type = type;
    }

    @Override
    protected int getRedstoneOutput(BlockState state) {
        return state.get(POWERED) != false ? 15 : 0;
    }

    @Override
    protected BlockState setRedstoneOutput(BlockState state, int rsOut) {
        return (BlockState)state.with(POWERED, rsOut > 0);
    }

    @Override
    protected int getRedstoneOutput(World world, BlockPos pos) {
        List<Entity> list;
        Box lv = BOX.offset(pos);
        switch (this.type) {
            case EVERYTHING: {
                list = world.getOtherEntities(null, lv);
                break;
            }
            case MOBS: {
                list = world.getNonSpectatingEntities(LivingEntity.class, lv);
                break;
            }
            default: {
                return 0;
            }
        }
        if (!list.isEmpty()) {
            for (Entity lv2 : list) {
                if (lv2.canAvoidTraps()) continue;
                return 15;
            }
        }
        return 0;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
    }

    public static enum ActivationRule {
        EVERYTHING,
        MOBS;

    }
}

