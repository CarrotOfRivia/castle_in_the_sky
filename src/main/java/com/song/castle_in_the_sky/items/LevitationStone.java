package com.song.castle_in_the_sky.items;

import com.song.castle_in_the_sky.CastleInTheSky;
import com.song.castle_in_the_sky.config.ConfigCommon;
import com.song.castle_in_the_sky.effects.EffectRegister;
import com.song.castle_in_the_sky.structures.StructureRegister;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

public class LevitationStone extends Item {
    public LevitationStone(Item.Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(ItemStack itemStack, ServerLevel world, net.minecraft.world.entity.Entity entity, EquipmentSlot slot) {
        if (isActive(itemStack) && world.getGameTime() % 40 == 0 && entity instanceof LivingEntity living) {
            if (living.hasEffect(EffectRegister.SACRED_CASTLE_EFFECT)) {
                living.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 100));
            } else {
                living.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 100));
            }
        }
        super.inventoryTick(itemStack, world, entity, slot);
    }

    @Override
    public InteractionResult use(Level world, net.minecraft.world.entity.player.Player playerEntity, InteractionHand hand) {
        if (!world.isClientSide()) {
            int useDistance = (int) (ConfigCommon.LEVITATION_STONE_USE_PERCENT.get() * ConfigCommon.CASTLE_SPAWN_PROOF.get());
            if (playerEntity.blockPosition().closerThan(net.minecraft.core.Vec3i.ZERO, useDistance)) {
                playerEntity.sendSystemMessage(Component.translatable("info." + CastleInTheSky.MOD_ID + ".too_close_to_spawn", ConfigCommon.CASTLE_SPAWN_PROOF.get(), useDistance).withStyle(ChatFormatting.RED, ChatFormatting.BOLD));
                return super.use(world, playerEntity, hand);
            }
            ItemStack itemStack = playerEntity.getItemInHand(hand);
            CustomData.update(DataComponents.CUSTOM_DATA, itemStack, tag -> tag.putBoolean("active", !tag.getBoolean("active").orElse(false)));
            if (isActive(itemStack) && world instanceof ServerLevel serverLevel) {
                BlockPos target = serverLevel.findNearestMapStructure(StructureRegister.CASTLE_IN_THE_SKY_LOCATED, playerEntity.blockPosition(), 100, false);
                if (target != null) {
                    BlockPos center = target.offset(72, ConfigCommon.CASTLE_HEIGHT.get() + 72 - target.getY(), 72);
                    CustomData.update(DataComponents.CUSTOM_DATA, itemStack, tag -> {
                        tag.putInt("targetX", center.getX());
                        tag.putInt("targetY", center.getY());
                        tag.putInt("targetZ", center.getZ());
                    });
                    CastleInTheSky.LOGGER.info("Levitation stone target for {} set to {}", playerEntity.getName().getString(), center);
                } else {
                    CustomData.update(DataComponents.CUSTOM_DATA, itemStack, tag -> {
                        tag.remove("targetX");
                        tag.remove("targetY");
                        tag.remove("targetZ");
                    });
                    CastleInTheSky.LOGGER.info("Levitation stone found no castle target for {}", playerEntity.getName().getString());
                }
            }
        }
        return super.use(world, playerEntity, hand);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, Item.TooltipContext tooltipContext, TooltipDisplay tooltipDisplay, Consumer<Component> consumer, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, tooltipContext, tooltipDisplay, consumer, tooltipFlag);
        consumer.accept(Component.translatable("tooltip." + CastleInTheSky.MOD_ID + ".levitation_stone.line1").withStyle(ChatFormatting.GRAY));
        consumer.accept(Component.translatable("tooltip." + CastleInTheSky.MOD_ID + ".levitation_stone.line2").withStyle(ChatFormatting.GRAY));
        consumer.accept(Component.translatable("tooltip." + CastleInTheSky.MOD_ID + ".levitation_stone.line3").withStyle(ChatFormatting.GRAY));
        consumer.accept(Component.literal(isActive(itemStack) ? "ON" : "OFF").withStyle(ChatFormatting.GOLD));
    }

    public boolean isActive(ItemStack itemStack) {
        CustomData data = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = data.copyTag();
        return tag.getBoolean("active").orElse(false);
    }

    public static void showTargetParticles(Player player, ItemStack itemStack) {
        if (!(itemStack.getItem() instanceof LevitationStone) || !((LevitationStone) itemStack.getItem()).isActive(itemStack)) {
            return;
        }
        CompoundTag tag = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (!tag.contains("targetX") || !tag.contains("targetY") || !tag.contains("targetZ")) {
            return;
        }
        BlockPos entityPos = player.blockPosition();
        int targetX = tag.getInt("targetX").orElse(entityPos.getX());
        int targetY = tag.getInt("targetY").orElse(entityPos.getY());
        int targetZ = tag.getInt("targetZ").orElse(entityPos.getZ());
        double distance = Math.sqrt(entityPos.distSqr(new Vec3i(targetX, targetY, targetZ)));
        if (distance < 1.0D) {
            return;
        }
        double dx = (targetX - entityPos.getX()) / distance;
        double dy = (targetY - entityPos.getY() - 2) / distance;
        double dz = (targetZ - entityPos.getZ()) / distance;
        for (int i = 2; i < 50; i++) {
            player.level().addParticle(ParticleTypes.CLOUD, i * dx + entityPos.getX(), i * dy + entityPos.getY(), i * dz + entityPos.getZ(), 0, 0, 0);
        }
    }
}
