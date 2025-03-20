package dev.rurino.hasugoods.block;

import com.mojang.serialization.MapCodec;

import dev.rurino.hasugoods.Hasugoods;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class NesoBaseBlock extends AbstractNesoBaseBlock {

  static void initialize() {
    Hasugoods.LOGGER.debug("Neso base block initialized");
  }

  public NesoBaseBlock(Settings settings) {
    super(settings);
  }

  @Override
  public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
    return new NesoBaseBlockEntity(pos, state);
  }

  @Override
  protected MapCodec<? extends BlockWithEntity> getCodec() {
    return createCodec(NesoBaseBlock::new);
  }

  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state,
      BlockEntityType<T> type) {
    return validateTicker(type, ModBlockEntities.NESO_BASE_BLOCK_ENTITY_TYPE, NesoBaseBlockEntity::tick);
  }

}
