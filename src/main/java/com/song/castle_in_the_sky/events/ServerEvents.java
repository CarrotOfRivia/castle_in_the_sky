package com.song.castle_in_the_sky.events;

import com.song.castle_in_the_sky.blocks.block_entities.LaputaCoreBE;
import com.song.castle_in_the_sky.config.ConfigCommon;
import com.song.castle_in_the_sky.effects.EffectRegister;
import com.song.castle_in_the_sky.items.ItemsRegister;
import com.song.castle_in_the_sky.items.LevitationStone;
import com.song.castle_in_the_sky.network.ServerToClientInfoPacket;
import com.song.castle_in_the_sky.utils.CapabilityCastle;
import com.song.castle_in_the_sky.utils.MyTradingRecipe;
import com.song.castle_in_the_sky.utils.RandomTradeBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.event.ServerChatEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.song.castle_in_the_sky.CastleInTheSky.MOD_ID;
import static net.minecraft.world.damagesource.DamageTypes.PLAYER_ATTACK;

public class ServerEvents {
    private static final Set<String> DESTRUCTION_INCANTATIONS = new HashSet<>(Arrays.asList("BARUSU", "BALSE", "BALUS", "バルス", "巴鲁斯"));
    private static final int SEARCH_RADIUS = 5;
    private static final int SEARCH_RADIUS2 = SEARCH_RADIUS * SEARCH_RADIUS;
    private static final int SEARCH_HEIGHT=3;

    @SubscribeEvent
    public void onPlayerChat(final ServerChatEvent event){
        if (DESTRUCTION_INCANTATIONS.contains(event.getMessage().getString())){
            if(ConfigCommon.DISABLE_INCANTATION.get()){
                event.getPlayer().sendSystemMessage(Component.translatable("info."+ MOD_ID+".destruction_disabled").withStyle(ChatFormatting.GRAY, ChatFormatting.BOLD));
                return;
            }
            ServerPlayer player = event.getPlayer();
            player.level().getServer().executeIfPossible(()->incantationSpoken(player, event));

        }
    }

    private static void incantationSpoken(ServerPlayer player, final ServerChatEvent event){
        boolean found = false;
        AtomicBoolean warned = new AtomicBoolean(false);
        warned.set(player.getData(CapabilityCastle.CASTLE_CAPS).isIncantationWarned());
        if (player.getMainHandItem().getItem() instanceof LevitationStone){
            for (int dy=-SEARCH_HEIGHT; !found && dy<=SEARCH_HEIGHT; dy++){
                for (int dx=-SEARCH_RADIUS; !found && dx<+SEARCH_RADIUS; dx++){
                    for (int dz=-SEARCH_RADIUS; !found && dz<+SEARCH_RADIUS; dz++){
                        if (dx*dx+dz*dz < SEARCH_RADIUS2){
                            BlockEntity blockEntity = player.level().getBlockEntity(player.blockPosition().offset(dx, dy, dz));
                            if (blockEntity instanceof LaputaCoreBE && !((LaputaCoreBE) blockEntity).isActive()){
                                if (! warned.get()){
                                    player.sendSystemMessage(Component.translatable("info."+ MOD_ID+".destruction_warning").withStyle(ChatFormatting.RED, ChatFormatting.BOLD));
                                    CapabilityCastle.Data data = player.getData(CapabilityCastle.CASTLE_CAPS);
                                    data.setIncantationWarned(true);
                                    data.setWarningCD();
                                    if (ConfigCommon.SILENT_INCANTATION.get()){
                                        event.setCanceled(true);
                                    }
                                    return;
                                }
                                else {
                                    player.getInventory().removeItem(player.getMainHandItem());
                                    ((LaputaCoreBE) blockEntity).setDestroying(true);
                                    ((LaputaCoreBE) blockEntity).setActivatedInitPos(player.getEyePosition());
                                    player.getData(CapabilityCastle.CASTLE_CAPS).setIncantationWarned(false);
                                    found = true;
                                    for (ServerPlayer playerOther: ((ServerLevel)player.level()).players()){
                                        playerOther.sendSystemMessage(Component.translatable("info."+ MOD_ID+".incantation_casted", player.getName()).withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD));
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if(! found){
                player.sendSystemMessage(Component.translatable("info."+ MOD_ID+".crystal_not_found").withStyle(ChatFormatting.GRAY, ChatFormatting.BOLD));
            }
        }
        else {
            player.sendSystemMessage(Component.translatable("info."+ MOD_ID+".item_not_hold").withStyle(ChatFormatting.GRAY, ChatFormatting.BOLD));
        }

        if (ConfigCommon.SILENT_INCANTATION.get()){
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void tickCap(final PlayerTickEvent.Post event){
        if (! event.getEntity().level().isClientSide()){
            event.getEntity().getData(CapabilityCastle.CASTLE_CAPS).tick();
        }
    }

    @SubscribeEvent
    public void onVillageTradeRegister(VillagerTradesEvent event){
        for (MyTradingRecipe recipe: ConfigCommon.MY_TRADING_RECIPES){
            String profession = recipe.getStringProfession();
            ResourceLocation eventProfessionId = BuiltInRegistries.VILLAGER_PROFESSION.getKey(event.getType());
            String eventProfession = eventProfessionId.toString();
            if((recipe.getItem1()!=null || recipe.getItem2() != null)
                    && (eventProfession.equals(profession) || eventProfessionId.getPath().equals(profession))){
                int level = recipe.getLevel();
                List<VillagerTrades.ItemListing> tmp = event.getTrades().get(level);
                ArrayList<VillagerTrades.ItemListing> mutableTrades = new ArrayList<>(tmp);
                mutableTrades.add(
                        new RandomTradeBuilder(64, 25, 0.05f)
                                .setPrice(recipe.getItem1(), recipe.price1Min().get(), recipe.price1Max().get())
                                .setPrice2(recipe.getItem2(), recipe.price2Min().get(), recipe.price2Max().get())
                                .setForSale(recipe.getOutput(), recipe.outputMin().get(), recipe.outputMax().get())
                                .build());
                event.getTrades().put(level, mutableTrades);
            }
        }
    }

    @SubscribeEvent
    public void onBlockBreak(PlayerEvent.BreakSpeed event){
        Player playerEntity = event.getEntity();
        if(playerEntity.hasEffect(EffectRegister.SACRED_CASTLE_EFFECT) && !playerEntity.isCreative()){
            event.setCanceled(true);
            if(playerEntity.level().isClientSide()){
                playerEntity.displayClientMessage(Component.translatable(String.format("info.%s.sacred_castle_effect.break", MOD_ID)).withStyle(ChatFormatting.RED, ChatFormatting.BOLD), true);
            }
        }
    }

    @SubscribeEvent
    public void onBlockPlaced(BlockEvent.EntityPlaceEvent event){
        Entity entity = event.getEntity();
        if(entity instanceof Player && ((Player)entity).hasEffect(EffectRegister.SACRED_CASTLE_EFFECT) && !((Player) entity).isCreative()){
            event.setCanceled(true);
            if(entity instanceof ServerPlayer){
                PacketDistributor.sendToPlayer((ServerPlayer) entity,
                        new ServerToClientInfoPacket(Component.translatable(String.format("info.%s.sacred_castle_effect.place", MOD_ID)).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD)));
            }
        }
    }

    @SubscribeEvent
    public void onMobDrop(LivingDropsEvent event){
        DamageSource damageSource = event.getSource();
        if(damageSource.is(PLAYER_ATTACK)){
            Entity killer = damageSource.getEntity();
            if(killer instanceof LivingEntity && ((LivingEntity) killer).hasEffect(EffectRegister.SACRED_CASTLE_EFFECT)){
                LivingEntity dropper = event.getEntity();
                if(dropper.getRandom().nextDouble()< ConfigCommon.YELLOW_KEY_DROP_RATE.get()){
                    event.getDrops().add(new ItemEntity(dropper.level(), dropper.position().x, dropper.position().y, dropper.position().z, new ItemStack(ItemsRegister.YELLOW_KEY.get())));
                }
                if(dropper.getRandom().nextDouble()<ConfigCommon.BLUE_KEY_DROP_RATE.get()){
                    event.getDrops().add(new ItemEntity(dropper.level(), dropper.position().x, dropper.position().y, dropper.position().z, new ItemStack(ItemsRegister.BLUE_KEY.get())));
                }
                if(dropper.getRandom().nextDouble()<ConfigCommon.RED_KEY_DROP_RATE.get()){
                    event.getDrops().add(new ItemEntity(dropper.level(), dropper.position().x, dropper.position().y, dropper.position().z, new ItemStack(ItemsRegister.RED_KEY.get())));
                }
            }
        }
    }
}
