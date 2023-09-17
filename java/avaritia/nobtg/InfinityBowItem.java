package avaritia.nobtg;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.ForgeEventFactory;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class InfinityBowItem extends BowItem {
    public static int DRAW_TIME = 8;

    public InfinityBowItem(Item.Properties properties) {
        super(properties);
    }

    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level world, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        InteractionResultHolder<ItemStack> ret = ForgeEventFactory.onArrowNock(stack, world, player, hand, true);
        if (ret != null) {
            return ret;
        }
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    public void releaseUsing(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entity, int timeLeft) {
        if (entity instanceof Player player) {
            ItemStack ammoStack = player.getProjectile(stack);
            int drawTime = this.getUseDuration(stack) - timeLeft;
            if ((drawTime = ForgeEventFactory.onArrowLoose(stack, level, player, drawTime, true)) < 0) {
                return;
            }
            if (ammoStack.isEmpty()) {
                ammoStack = new ItemStack(Items.ARROW);
            }
            float VELOCITY_MULTIPLIER = 1.2f;
            float DAMAGE_MULTIPLIER = 5000.0f;
            float draw = InfinityBowItem.getPowerForTime(drawTime);
            float powerForTime = draw * VELOCITY_MULTIPLIER;
            if ((double) powerForTime >= 0.1) {
                if (!level.isClientSide) {
                    int k;
                    ArrowItem arrowitem = (ArrowItem) (ammoStack.getItem() instanceof ArrowItem ? ammoStack.getItem() : Items.ARROW);
                    AbstractArrow arrowEntity = this.customArrow(arrowitem.createArrow(level, ammoStack, player));
                    if (arrowEntity instanceof Arrow arrow) {
                        arrow.setEffectsFromItem(ammoStack);
                    }
                    arrowEntity.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0f, powerForTime * 3.0f, 0.01f);
                    if (draw == 1.0f) {
                        arrowEntity.setCritArrow(true);
                    }
                    arrowEntity.setBaseDamage(arrowEntity.getBaseDamage() * (double) DAMAGE_MULTIPLIER);
                    int j = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, stack);
                    if (j > 0) {
                        arrowEntity.setBaseDamage(arrowEntity.getBaseDamage() + (double) j * 0.5 + 0.5);
                    }
                    if ((k = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, stack)) > 0) {
                        arrowEntity.setKnockback(k);
                    }
                    if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FLAMING_ARROWS, stack) > 0) {
                        arrowEntity.setSecondsOnFire(100);
                    }
                    arrowEntity.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                    level.addFreshEntity(arrowEntity);
                }
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0f, 1.0f / (level.random.nextFloat() * 0.4f + 1.2f) + powerForTime * 0.5f);
                player.awardStat(Stats.ITEM_USED.get(this));
            }
        }
    }

    public @NotNull AbstractArrow customArrow(AbstractArrow arrow) {
        if (arrow.getType() != EntityType.ARROW && arrow.getType() != EntityType.SPECTRAL_ARROW) {
            return arrow;
        }
        return new Arrow(arrow.level, (LivingEntity) Objects.requireNonNull(arrow.getOwner()));
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        if (entity.getAge() >= 0) {
            entity.setExtendedLifetime();
        }
        return super.onEntityItemUpdate(stack, entity);
    }
}