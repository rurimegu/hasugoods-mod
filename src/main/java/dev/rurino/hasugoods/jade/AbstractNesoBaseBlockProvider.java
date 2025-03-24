package dev.rurino.hasugoods.jade;

import dev.rurino.hasugoods.block.AbstractNesoBaseBlockEntity;
import dev.rurino.hasugoods.item.neso.NesoItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

class AbstractNesoBaseBlockProvider implements IBlockComponentProvider {

  @Override
  public void appendTooltip(ITooltip tooltip, BlockAccessor blockAccessor, IPluginConfig pluginConfig) {
    if (blockAccessor.getBlockEntity() instanceof AbstractNesoBaseBlockEntity blockEntity) {
      ItemStack itemStack = blockEntity.getItemStack();
      if (itemStack.getItem() instanceof NesoItem nesoItem) {
        Text text = nesoItem.getTooltip(itemStack);
        if (text != null) {
          tooltip.add(text);
        }
      }
    }
  }

  @Override
  public Identifier getUid() {
    return HasuJadePlugin.NESO_BASE_BLOCK;
  }

}
