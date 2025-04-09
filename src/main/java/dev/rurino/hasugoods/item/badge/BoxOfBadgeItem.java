package dev.rurino.hasugoods.item.badge;

import dev.rurino.hasugoods.Hasugoods;
import dev.rurino.hasugoods.util.ItemStackUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;

public class BoxOfBadgeItem extends Item {

  private static final IntValue NUM_BADGE_IN_BOX = Hasugoods.CONFIG.loot.numBadgeInBox;

  public BoxOfBadgeItem(Settings settings) {
    super(settings);
  }

  @Override
  public ActionResult use(World world, PlayerEntity user, Hand hand) {
    if (world.isClient)
      return super.use(world, user, hand);
    int numBadges = NUM_BADGE_IN_BOX.get();
    return ItemStackUtils.replaceItemsToPlayerOrDrop(user, BadgeItem.UNOPENED_BADGE, numBadges);
  }
}
