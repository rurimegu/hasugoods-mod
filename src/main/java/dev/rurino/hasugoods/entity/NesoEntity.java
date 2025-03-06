package dev.rurino.hasugoods.entity;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import dev.rurino.hasugoods.Hasugoods;
import dev.rurino.hasugoods.item.neso.NesoItem;
import dev.rurino.hasugoods.util.OshiUtils;
import dev.rurino.hasugoods.util.OshiUtils.NesoSize;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class NesoEntity extends LivingEntity {
  // #region Static fields
  protected static record NesoEntityEntry(RegistryKey<EntityType<?>> key, EntityType<NesoEntity> entity) {
  }

  protected static final Map<String, NesoEntityEntry> ALL_NESOS = new HashMap<>();

  public static Optional<EntityType<NesoEntity>> getNesoEntityType(String oshiKey, NesoSize size) {
    return Optional.ofNullable(ALL_NESOS.get(OshiUtils.nesoKey(oshiKey, size))).map(entry -> entry.entity());
  }

  public static List<EntityType<NesoEntity>> getAllNesos(NesoSize size) {
    return ALL_NESOS.values().stream()
        .filter(entry -> entry.key().getValue().getPath().endsWith("_" + size.name().toLowerCase()))
        .map(entry -> entry.entity()).toList();
  }

  public static List<EntityType<NesoEntity>> getAllNesos() {
    return ALL_NESOS.values().stream().map(entry -> entry.entity()).toList();
  }

  public static EntityType<NesoEntity> registerNeso(String oshiKey, NesoSize size) {
    String itemKey = OshiUtils.nesoKey(oshiKey, size);
    RegistryKey<EntityType<?>> key = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Hasugoods.id(itemKey));
    float width = switch (size) {
      case SMALL -> 0.4f;
      case MEDIUM -> 0.8f;
      case LARGE -> 1.6f;
    };
    float height = switch (size) {
      case SMALL -> 0.24f;
      case MEDIUM -> 0.48f;
      case LARGE -> 0.96f;
    };
    EntityType<NesoEntity> entityType = EntityType.Builder.<NesoEntity>create(
        (type, world) -> new NesoEntity(type, world, oshiKey, size), SpawnGroup.MISC).dimensions(width, height)
        .eyeHeight(height * 0.4f)
        .dropsNothing()
        .build(key);
    entityType = ModEntities.register(key, entityType);

    ALL_NESOS.put(itemKey, new NesoEntityEntry(key, entityType));
    FabricDefaultAttributeRegistry.register(entityType, NesoEntity.createLivingAttributes());
    return entityType;
  }

  public static void Initialize() {
    for (NesoSize size : NesoSize.values()) {
      registerNeso(OshiUtils.KAHO_KEY, size);
    }
  }
  // #endregion Static fields

  protected final String oshiKey;
  protected final NesoSize nesoSize;

  public NesoEntity(EntityType<? extends LivingEntity> type, World world, String oshiKey, NesoSize size) {
    super(type, world);
    this.oshiKey = oshiKey;
    this.nesoSize = size;
  }

  @Override
  public boolean damage(ServerWorld world, DamageSource source, float amount) {
    if (!source.isIn(DamageTypeTags.IS_FIRE)) {
      amount = 0;
    }
    return super.damage(world, source, amount);
  }

  @Override
  public void equipStack(EquipmentSlot slot, ItemStack stack) {
    // Cannot equip items
    Hasugoods.LOGGER.warn("Cannot equip items to NesoEntity: {}", stack);
  }

  @Override
  public Iterable<ItemStack> getArmorItems() {
    return Collections.emptyList();
  }

  @Override
  public ItemStack getEquippedStack(EquipmentSlot slot) {
    return ItemStack.EMPTY;
  }

  @Override
  public Arm getMainArm() {
    return Arm.RIGHT;
  }

  @Override
  public ActionResult interact(PlayerEntity player, Hand hand) {
    if (player.getWorld().isClient)
      return ActionResult.CONSUME;

    ItemStack entityItem = new ItemStack(getNesoItem());
    this.dropStack((ServerWorld) player.getWorld(), entityItem);
    this.discard();
    return ActionResult.SUCCESS;
  }

  public String getOshiKey() {
    return oshiKey;
  }

  public NesoSize getNesoSize() {
    return nesoSize;
  }

  public NesoItem getNesoItem() {
    Optional<NesoItem> item = NesoItem.getNesoItem(oshiKey, nesoSize);
    if (item.isEmpty()) {
      Hasugoods.LOGGER.error("NesoItem not found: {} {}", oshiKey, nesoSize);
    }
    return item.get();
  }
}
