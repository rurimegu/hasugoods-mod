package dev.rurino.hasugoods.item.badge;

import dev.rurino.hasugoods.Hasugoods;
import dev.rurino.hasugoods.util.ItemStackUtils;
import dev.rurino.hasugoods.util.config.HcVal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class BoxOfBadgeItem extends Item {
  public static final HcVal.Int NUM_BADGE_IN_BOX = Hasugoods.CONFIG.getInt("numBadgeInBox", 16);

  public BoxOfBadgeItem(Settings settings) {
    super(settings);
  }

  @Override
  public ActionResult use(World world, PlayerEntity user, Hand hand) {
    if (world.isClient)
      return super.use(world, user, hand);
    int numBadges = NUM_BADGE_IN_BOX.val();
    int maxNumStack = BadgeItem.UNOPENED_BADGE.getMaxCount();
    if (numBadges > maxNumStack) {
      ItemStackUtils.giveItemsToPlayerOrDrop(user, BadgeItem.UNOPENED_BADGE, numBadges - maxNumStack);
      numBadges = BadgeItem.UNOPENED_BADGE.getMaxCount();
    }
    ItemStack unopenedBadgeStack = new ItemStack(BadgeItem.UNOPENED_BADGE, numBadges);
    return ActionResult.SUCCESS.withNewHandStack(unopenedBadgeStack);
  }
}
