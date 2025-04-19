package dev.rurino.hasugoods.block;

import dev.rurino.hasugoods.Hasugoods;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
  public static final BlockEntityType<NesoBaseBlockEntity> NESO_BASE_BLOCK_ENTITY_TYPE = register("neso_base",
      NesoBaseBlockEntity::new, ModBlocks.NESO_BASE_BLOCK);
  public static final BlockEntityType<PositionZeroBlockEntity> POSITION_ZERO_BLOCK_ENTITY_TYPE = register(
      "position_zero", PositionZeroBlockEntity::new, ModBlocks.POSITION_ZERO_BLOCK);

  private static <T extends BlockEntity> BlockEntityType<T> register(
      String name,
      FabricBlockEntityTypeBuilder.Factory<? extends T> entityFactory,
      Block... blocks) {
    Identifier id = Hasugoods.id(name);
    Hasugoods.LOGGER.debug("Register block entity: {}", id);
    return Registry.register(Registries.BLOCK_ENTITY_TYPE, id,
        FabricBlockEntityTypeBuilder.<T>create(entityFactory, blocks).build());
  }

  public static void initialize() {
    NesoBaseBlockEntity.initialize();
    PositionZeroBlockEntity.initialize();
  }
}
