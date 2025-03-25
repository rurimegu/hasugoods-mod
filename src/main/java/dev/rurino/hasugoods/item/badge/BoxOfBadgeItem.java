package dev.rurino.hasugoods.item.badge;

import dev.rurino.hasugoods.util.ItemStackUtils;
import dev.rurino.hasugoods.util.config.HcVal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class BoxOfBadgeItem extends Item {
  public static final HcVal.Int NUM_BADGE_IN_BOX = BadgeItem.HC_LOOT.getInt("numBadgeInBox", 16);

  public BoxOfBadgeItem(Settings settings) {
    super(settings);
  }

  @Override
  public ActionResult use(World world, PlayerEntity user, Hand hand) {
    if (world.isClient)
      return super.use(world, user, hand);
    int numBadges = NUM_BADGE_IN_BOX.val();
    return ItemStackUtils.replaceItemsToPlayerOrDrop(user, BadgeItem.UNOPENED_BADGE, numBadges);
  }
}
