/*
 * Decompiled with CFR 0.1.1 (FabricMC 57d88659).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.SideShapeType;
import net.minecraft.block.WoodType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.HangingSignBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class WallHangingSignBlock
extends AbstractSignBlock {
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    public static final VoxelShape NORTH_SOUTH_COLLISION_SHAPE = Block.createCuboidShape(0.0, 14.0, 6.0, 16.0, 16.0, 10.0);
    public static final VoxelShape EAST_WEST_COLLISION_SHAPE = Block.createCuboidShape(6.0, 14.0, 0.0, 10.0, 16.0, 16.0);
    public static final VoxelShape NORTH_SOUTH_SHAPE = VoxelShapes.union(NORTH_SOUTH_COLLISION_SHAPE, Block.createCuboidShape(1.0, 0.0, 7.0, 15.0, 10.0, 9.0));
    public static final VoxelShape EAST_WEST_SHAPE = VoxelShapes.union(EAST_WEST_COLLISION_SHAPE, Block.createCuboidShape(7.0, 0.0, 1.0, 9.0, 10.0, 15.0));
    private static final Map<Direction, VoxelShape> OUTLINE_SHAPES = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, NORTH_SOUTH_SHAPE, Direction.SOUTH, NORTH_SOUTH_SHAPE, Direction.EAST, EAST_WEST_SHAPE, Direction.WEST, EAST_WEST_SHAPE));

    public WallHangingSignBlock(AbstractBlock.Settings arg, WoodType arg2) {
        super(arg.sounds(arg2.hangingSignSoundType()), arg2);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.NORTH)).with(WATERLOGGED, false));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof SignBlockEntity) {
            SignBlockEntity lv = (SignBlockEntity)blockEntity;
            ItemStack lv2 = player.getStackInHand(hand);
            if (!lv.shouldRunCommand(player) && lv2.getItem() instanceof BlockItem) {
                return ActionResult.PASS;
            }
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Override
    public String getTranslationKey() {
        return this.asItem().getTranslationKey();
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return OUTLINE_SHAPES.get(state.get(FACING));
    }

    @Override
    public VoxelShape getSidesShape(BlockState state, BlockView world, BlockPos pos) {
        return this.getOutlineShape(state, world, pos, ShapeContext.absent());
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        switch (state.get(FACING)) {
            case EAST: 
            case WEST: {
                return EAST_WEST_COLLISION_SHAPE;
            }
        }
        return NORTH_SOUTH_COLLISION_SHAPE;
    }

    public boolean canAttachAt(BlockState state, WorldView world, BlockPos pos) {
        Direction lv = state.get(FACING).rotateYClockwise();
        Direction lv2 = state.get(FACING).rotateYCounterclockwise();
        return this.canAttachTo(world, state, pos.offset(lv), lv2) || this.canAttachTo(world, state, pos.offset(lv2), lv);
    }

    public boolean canAttachTo(WorldView world, BlockState state, BlockPos toPos, Direction direction) {
        BlockState lv = world.getBlockState(toPos);
        if (lv.isIn(BlockTags.WALL_HANGING_SIGNS)) {
            return lv.get(FACING).getAxis().test(state.get(FACING));
        }
        return lv.isSideSolid(world, toPos, direction, SideShapeType.FULL);
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState lv = this.getDefaultState();
        FluidState lv2 = ctx.getWorld().getFluidState(ctx.getBlockPos());
        World lv3 = ctx.getWorld();
        BlockPos lv4 = ctx.getBlockPos();
        for (Direction lv5 : ctx.getPlacementDirections()) {
            Direction lv6;
            if (!lv5.getAxis().isHorizontal() || lv5.getAxis().test(ctx.getSide()) || !(lv = (BlockState)lv.with(FACING, lv6 = lv5.getOpposite())).canPlaceAt(lv3, lv4) || !this.canAttachAt(lv, lv3, lv4)) continue;
            return (BlockState)lv.with(WATERLOGGED, lv2.getFluid() == Fluids.WATER);
        }
        return null;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (direction.getAxis() == state.get(FACING).rotateYClockwise().getAxis() && !state.canPlaceAt(world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new HangingSignBlockEntity(pos, state);
    }

    @Override
    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return false;
    }
}

