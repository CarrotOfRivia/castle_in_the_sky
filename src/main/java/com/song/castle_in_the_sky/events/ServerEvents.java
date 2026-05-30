package com.song.castle_in_the_sky.events;

import com.song.castle_in_the_sky.CastleInTheSky;
import com.song.castle_in_the_sky.blocks.BlockRegister;
import com.song.castle_in_the_sky.blocks.LaputaCore;
import com.song.castle_in_the_sky.config.ConfigCommon;
import com.song.castle_in_the_sky.effects.EffectRegister;
import com.song.castle_in_the_sky.items.ItemsRegister;
import com.song.castle_in_the_sky.items.LevitationStone;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.ServerChatEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static net.minecraft.world.damagesource.DamageTypes.PLAYER_ATTACK;

public class ServerEvents {
    private static final Set<String> DESTRUCTION_INCANTATIONS =
            new HashSet<>(Arrays.asList("BARUSU", "BALSE", "BALUS", "バルス", "巴鲁斯"));
    private static final Map<UUID, Integer> incantationWarningTicks = new HashMap<>();

    @SubscribeEvent
    public void onPlayerChat(final ServerChatEvent event) {
        if (!DESTRUCTION_INCANTATIONS.contains(event.getRawText())) {
            return;
        }
        ServerPlayer player = event.getPlayer();
        if (ConfigCommon.SILENT_INCANTATION.get()) {
            event.setCanceled(true);
        }
        if (ConfigCommon.DISABLE_INCANTATION.get()) {
            player.sendSystemMessage(Component.translatable("info." + CastleInTheSky.MOD_ID + ".destruction_disabled").withStyle(ChatFormatting.GRAY, ChatFormatting.BOLD));
            return;
        }
        if (!(player.getMainHandItem().getItem() instanceof LevitationStone)) {
            player.sendSystemMessage(Component.translatable("info." + CastleInTheSky.MOD_ID + ".item_not_hold").withStyle(ChatFormatting.GRAY, ChatFormatting.BOLD));
            return;
        }
        UUID playerId = player.getUUID();
        int ticks = incantationWarningTicks.getOrDefault(playerId, 0);
        if (ticks > 0) {
            boolean triggered = false;
            for (int dy = -3; dy <= 3 && !triggered; dy++) {
                for (int dx = -5; dx <= 5 && !triggered; dx++) {
                    for (int dz = -5; dz <= 5 && !triggered; dz++) {
                        if (dx * dx + dz * dz > 25) {
                            continue;
                        }
                        var target = player.blockPosition().offset(dx, dy, dz);
                        BlockState targetState = player.level().getBlockState(target);
                        if (targetState.is(BlockRegister.LAPUTA_CORE.get()) && !targetState.getValue(LaputaCore.DESTROYING)) {
                            player.level().setBlock(target, targetState.setValue(LaputaCore.DESTROYING, true), net.minecraft.world.level.block.Block.UPDATE_ALL);
                            player.level().scheduleTick(target, BlockRegister.LAPUTA_CORE.get(), 1);
                            triggered = true;
                        }
                    }
                }
            }
            if (triggered) {
                player.getInventory().removeItem(player.getMainHandItem());
                incantationWarningTicks.put(playerId, 0);
                player.sendSystemMessage(Component.translatable("info." + CastleInTheSky.MOD_ID + ".incantation_casted", player.getName()).withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD));
                return;
            }
            player.sendSystemMessage(Component.translatable("info." + CastleInTheSky.MOD_ID + ".crystal_not_found").withStyle(ChatFormatting.GRAY, ChatFormatting.BOLD));
        }
        incantationWarningTicks.put(playerId, 200);
        player.sendSystemMessage(Component.translatable("info." + CastleInTheSky.MOD_ID + ".destruction_warning").withStyle(ChatFormatting.RED, ChatFormatting.BOLD));
    }

    @SubscribeEvent
    public void onBlockBreak(BreakBlockEvent event) {
        Player player = event.getPlayer();
        if (player != null && player.hasEffect(EffectRegister.SACRED_CASTLE_EFFECT) && !player.isCreative()) {
            event.setCanceled(true);
            event.setNotifyClient(true);
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.sendOverlayMessage(Component.translatable("info." + CastleInTheSky.MOD_ID + ".sacred_castle_effect.break").withStyle(ChatFormatting.RED, ChatFormatting.BOLD));
            }
        }
    }

    @SubscribeEvent
    public void onBlockPlaced(BlockEvent.EntityPlaceEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player player && player.hasEffect(EffectRegister.SACRED_CASTLE_EFFECT) && !player.isCreative()) {
            event.setCanceled(true);
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.sendSystemMessage(Component.translatable("info." + CastleInTheSky.MOD_ID + ".sacred_castle_effect.place").withStyle(ChatFormatting.RED, ChatFormatting.BOLD));
            }
        }
    }

    @SubscribeEvent
    public void onMobDrop(LivingDropsEvent event) {
        DamageSource damageSource = event.getSource();
        if (!damageSource.is(PLAYER_ATTACK)) {
            return;
        }
        Entity killer = damageSource.getEntity();
        if (!(killer instanceof LivingEntity living) || !living.hasEffect(EffectRegister.SACRED_CASTLE_EFFECT)) {
            return;
        }
        LivingEntity dropper = event.getEntity();
        if (dropper.getRandom().nextDouble() < ConfigCommon.YELLOW_KEY_DROP_RATE.get()) {
            event.getDrops().add(new ItemEntity(dropper.level(), dropper.getX(), dropper.getY(), dropper.getZ(), new ItemStack(ItemsRegister.YELLOW_KEY.get())));
        }
        if (dropper.getRandom().nextDouble() < ConfigCommon.BLUE_KEY_DROP_RATE.get()) {
            event.getDrops().add(new ItemEntity(dropper.level(), dropper.getX(), dropper.getY(), dropper.getZ(), new ItemStack(ItemsRegister.BLUE_KEY.get())));
        }
        if (dropper.getRandom().nextDouble() < ConfigCommon.RED_KEY_DROP_RATE.get()) {
            event.getDrops().add(new ItemEntity(dropper.level(), dropper.getX(), dropper.getY(), dropper.getZ(), new ItemStack(ItemsRegister.RED_KEY.get())));
        }
    }

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity().level().isClientSide()) {
            if (event.getEntity().level().dimension() == Level.OVERWORLD) {
                LevitationStone.showTargetParticles(event.getEntity(), event.getEntity().getMainHandItem());
                LevitationStone.showTargetParticles(event.getEntity(), event.getEntity().getOffhandItem());
            }
            return;
        }
        UUID id = event.getEntity().getUUID();
        int ticks = incantationWarningTicks.getOrDefault(id, 0);
        if (ticks <= 0) {
            return;
        }
        incantationWarningTicks.put(id, ticks - 1);
    }
}
