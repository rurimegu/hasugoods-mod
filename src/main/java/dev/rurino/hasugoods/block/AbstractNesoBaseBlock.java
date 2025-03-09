package dev.rurino.hasugoods.block;

import com.mojang.serialization.MapCodec;

import dev.rurino.hasugoods.item.neso.NesoItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public abstract class AbstractNesoBaseBlock extends BlockWithEntity {
  public static final EnumProperty<Direction> FACING = HorizontalFacingBlock.FACING;

  public AbstractNesoBaseBlock(Settings settings) {
    super(settings);
    this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
  }

  @Override
  public BlockState getPlacementState(ItemPlacementContext ctx) {
    return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
  }

  @Override
  protected BlockState rotate(BlockState state, BlockRotation rotation) {
    return state.with(FACING, rotation.rotate(state.get(FACING)));
  }

  @Override
  protected BlockState mirror(BlockState state, BlockMirror mirror) {
    return state.rotate(mirror.getRotation(state.get(FACING)));
  }

  @Override
  protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
    builder.add(FACING);
  }

  @Override
  public abstract BlockEntity createBlockEntity(BlockPos pos, BlockState state);

  @Override
  protected abstract MapCodec<? extends BlockWithEntity> getCodec();

  @Override
  protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
    if (world.isClient || !(world.getBlockEntity(pos) instanceof AbstractNesoBaseBlockEntity blockEntity)) {
      return super.onUse(state, world, pos, player, hit);
    }
    ItemStack prevItemStack = blockEntity.getItemStack();
    ItemStack stackInHand = player.getStackInHand(player.getActiveHand());
    ActionResult actionResult = ActionResult.PASS;

    if (!prevItemStack.isEmpty()) {
      Block.dropStack(world, pos, hit.getSide(), prevItemStack);
      blockEntity.setItemStack(ItemStack.EMPTY);
      actionResult = ActionResult.SUCCESS;
    }

    if (!stackInHand.isEmpty() && stackInHand.getItem() instanceof NesoItem) {
      blockEntity.setItemStack(stackInHand.copyWithCount(1));
      stackInHand.decrement(1);
      actionResult = ActionResult.SUCCESS;
    }

    if (actionResult == ActionResult.SUCCESS) {
      player.swingHand(player.getActiveHand(), true);
    }
    return actionResult;
  }

}
