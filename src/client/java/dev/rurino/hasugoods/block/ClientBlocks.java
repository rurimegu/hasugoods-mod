package dev.rurino.hasugoods.block;

import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class ClientBlocks {
  public static void initialize() {
    BlockEntityRendererFactories.register(ModBlockEntities.NESO_BASE_BLOCK_ENTITY_TYPE,
        NesoBaseBlockEntityRenderer::new);
    BlockEntityRendererFactories.register(ModBlockEntities.POSITION_ZERO_BLOCK_ENTITY_TYPE,
        NesoBaseBlockEntityRenderer::new);
  }
}
