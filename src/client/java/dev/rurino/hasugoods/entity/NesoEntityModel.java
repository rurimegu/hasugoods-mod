package dev.rurino.hasugoods.entity;

import java.util.List;
import java.util.Map;

import dev.rurino.hasugoods.item.neso.NesoItem;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.item.ItemStack;

public class NesoEntityModel extends EntityModel<LivingEntityRenderState> {

  protected final NesoItem item;
  protected final ItemStack stack;

  protected NesoEntityModel(NesoItem item) {
    super(new ModelPart(List.of(), Map.of()));
    this.item = item;
    this.stack = new ItemStack(item);
  }

  public NesoItem getItem() {
    return item;
  }

  public ItemStack getItemStack() {
    return stack;
  }

  public float getShadowRadius() {
    return switch (item.getNesoSize()) {
      case SMALL -> 0.2F;
      case MEDIUM -> 0.4F;
      case LARGE -> 0.8F;
    };
  }

}
