package dev.rurino.hasugoods.entity;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import dev.rurino.hasugoods.Hasugoods;
import dev.rurino.hasugoods.item.neso.NesoItem;
import dev.rurino.hasugoods.util.CharaUtils;
import dev.rurino.hasugoods.util.CharaUtils.NesoSize;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class NesoEntity extends LivingEntity {
  // #region Static fields
  protected static record NesoEntityEntry(RegistryKey<EntityType<?>> key, EntityType<NesoEntity> entity) {
  }

  protected static final Map<String, NesoEntityEntry> ALL_NESOS = new HashMap<>();

  public static Optional<EntityType<NesoEntity>> getNesoEntityType(String charaKey, NesoSize size) {
    return Optional.ofNullable(ALL_NESOS.get(CharaUtils.nesoKey(charaKey, size))).map(entry -> entry.entity());
  }

  public static List<EntityType<NesoEntity>> getAllNesos(NesoSize size) {
    return ALL_NESOS.values().stream()
        .filter(entry -> entry.key().getValue().getPath().endsWith("_" + size.name().toLowerCase()))
        .map(entry -> entry.entity()).toList();
  }

  public static List<EntityType<NesoEntity>> getAllNesos() {
    return ALL_NESOS.values().stream().map(entry -> entry.entity()).toList();
  }

  public static EntityType<NesoEntity> registerNeso(String charaKey, NesoSize size) {
    String itemKey = CharaUtils.nesoKey(charaKey, size);
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
        (type, world) -> new NesoEntity(type, world, charaKey, size), SpawnGroup.MISC).dimensions(width, height)
        .eyeHeight(height * 0.4f)
        .dropsNothing()
        .build(key);
    entityType = ModEntities.register(key, entityType);

    ALL_NESOS.put(itemKey, new NesoEntityEntry(key, entityType));
    FabricDefaultAttributeRegistry.register(entityType, NesoEntity.createNesoAttributes(size));
    return entityType;
  }

  public static DefaultAttributeContainer.Builder createNesoAttributes(NesoSize size) {
    float maxHealth = switch (size) {
      case SMALL -> 10;
      case MEDIUM -> 20;
      case LARGE -> 40;
    };
    return createLivingAttributes().add(EntityAttributes.MAX_HEALTH, maxHealth);
  }

  public static NesoEntity spawnFromItemStack(EntityType<NesoEntity> entityType, ServerWorld world, ItemStack stack,
      PlayerEntity player, BlockPos pos,
      SpawnReason spawnReason, boolean alignPosition, boolean invertY, float initYaw) {
    Consumer<NesoEntity> consumer;
    if (stack != null) {
      consumer = EntityType.copier(world, stack, player);
    } else {
      consumer = (entity) -> {
      };
    }
    NesoEntity entity = entityType.create(world, consumer, pos, spawnReason, alignPosition, invertY);
    if (entity != null) {
      entity.rotate(initYaw, 0);
      world.spawnEntityAndPassengers(entity);
    }

    return entity;
  }

  public static void Initialize() {
    for (NesoItem neso : NesoItem.getAllNesos()) {
      registerNeso(neso.getCharaKey(), neso.getNesoSize());
    }
  }
  // #endregion Static fields

  protected final String charaKey;
  protected final NesoSize nesoSize;

  public NesoEntity(EntityType<? extends LivingEntity> type, World world, String charaKey, NesoSize size) {
    super(type, world);
    this.charaKey = charaKey;
    this.nesoSize = size;
  }

  @Override
  public boolean damage(ServerWorld world, DamageSource source, float amount) {
    if (getNesoSize() == NesoSize.LARGE) {
      return false;
    }
    if (!source.isIn(DamageTypeTags.IS_FIRE)) {
      if (!source.isIn(DamageTypeTags.IS_PLAYER_ATTACK)) {
        return false;
      }
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

    this.dropStack((ServerWorld) player.getWorld(), convertToNesoItemStack());
    this.discard();
    return ActionResult.SUCCESS;
  }

  @Override
  protected float getMaxRelativeHeadRotation() {
    return 0f;
  }

  public String getCharaKey() {
    return charaKey;
  }

  public NesoSize getNesoSize() {
    return nesoSize;
  }

  public NesoItem getNesoItem() {
    Optional<NesoItem> item = NesoItem.getNesoItem(charaKey, nesoSize);
    if (item.isEmpty()) {
      Hasugoods.LOGGER.error("NesoItem not found: {} {}", charaKey, nesoSize);
    }
    return item.get();
  }

  protected ItemStack convertToNesoItemStack() {
    ItemStack stack = new ItemStack(getNesoItem());
    // Set custom name
    Text text = this.getCustomName();
    if (text != null) {
      stack.set(DataComponentTypes.CUSTOM_NAME, text);
    }
    return stack;
  }
}
