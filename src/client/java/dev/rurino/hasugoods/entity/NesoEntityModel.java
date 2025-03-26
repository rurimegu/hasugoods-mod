package dev.rurino.hasugoods.entity;

import java.util.List;
import java.util.Map;

import dev.rurino.hasugoods.item.neso.NesoItem;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.item.ItemStack;

public class NesoEntityModel extends EntityModel<LivingEntityRenderState> {

  public static final CustomModelDataComponent NESO_3D_CUSTOM_MODEL_DATA = new CustomModelDataComponent(
      List.of(), List.of(true), List.of(), List.of());

  private final NesoItem item;
  private final ItemStack stack;

  protected NesoEntityModel(NesoItem item) {
    super(new ModelPart(List.of(), Map.of()));
    this.item = item;
    this.stack = new ItemStack(item);
    this.stack.set(DataComponentTypes.CUSTOM_MODEL_DATA, NESO_3D_CUSTOM_MODEL_DATA);
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
