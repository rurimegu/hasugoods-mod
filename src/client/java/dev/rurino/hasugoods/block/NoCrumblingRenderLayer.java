package dev.rurino.hasugoods.block;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.render.RenderLayer;

public class NoCrumblingRenderLayer extends RenderLayer {
  private static final Map<RenderLayer, RenderLayer> MEMOIZED = new HashMap<>();

  public NoCrumblingRenderLayer(RenderLayer from) {
    super(from.toString(), from.getVertexFormat(), from.getDrawMode(), from.getExpectedBufferSize(), false,
        from.isTranslucent(), from::startDrawing, from::endDrawing);
  }

  public static RenderLayer get(RenderLayer from) {
    if (!from.hasCrumbling())
      return from;

    return MEMOIZED.computeIfAbsent(from, NoCrumblingRenderLayer::new);
  }
}
