package dev.rurino.hasugoods.block;

import com.mojang.serialization.MapCodec;

import dev.rurino.hasugoods.Hasugoods;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PositionZeroBlock extends AbstractNesoBaseBlock {

  static void initialize() {
    Hasugoods.LOGGER.debug("Position zero block initialized");
  }

  public PositionZeroBlock(Settings settings) {
    super(settings);
  }

  @Override
  protected MapCodec<? extends BlockWithEntity> getCodec() {
    return createCodec(PositionZeroBlock::new);
  }

  @Override
  public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
    return new PositionZeroBlockEntity(pos, state);
  }

  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state,
      BlockEntityType<T> type) {
    return validateTicker(type, ModBlockEntities.POSITION_ZERO_BLOCK_ENTITY_TYPE, PositionZeroBlockEntity::tick);
  }

  @Override
  public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
    if (!world.isClient && world.getBlockEntity(pos) instanceof PositionZeroBlockEntity blockEntity) {
      blockEntity.unlinkNesoBases();
    }
    return super.onBreak(world, pos, state, player);
  }
}
