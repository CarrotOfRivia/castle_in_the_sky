package com.song.castle_in_the_sky.events;

import com.song.castle_in_the_sky.CastleInTheSky;
import com.song.castle_in_the_sky.config.ConfigCommon;
import com.song.castle_in_the_sky.effects.EffectRegister;
import com.song.castle_in_the_sky.features.StructureFeatureRegister;
import com.song.castle_in_the_sky.items.ItemsRegister;
import com.song.castle_in_the_sky.network.Channel;
import com.song.castle_in_the_sky.network.ClientHandlerClass;
import com.song.castle_in_the_sky.network.ServerToClientInfoPacket;
import com.song.castle_in_the_sky.utils.MyTradingRecipe;
import com.song.castle_in_the_sky.utils.RandomTradeBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.StructureSpawnListGatherEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fmllegacy.network.PacketDistributor;

import java.util.Objects;

public class ServerEvents {

    @SubscribeEvent
    public void onBiomeLoading(BiomeLoadingEvent event){
        if(event.getCategory() == Biome.BiomeCategory.OCEAN){
            event.getGeneration().addStructureStart(StructureFeatureRegister.CONFIGURED_CASTLE_IN_THE_SKY);
        }
    }

    @SubscribeEvent
    public void onVillageTradeRegister(VillagerTradesEvent event){
        for (MyTradingRecipe recipe: ConfigCommon.MY_TRADING_RECIPES){
            if((recipe.getItem1()!=null || recipe.getItem2()!=null) && Objects.requireNonNull(event.getType().getRegistryName()).toString().equals(recipe.getStringProfession())){
                event.getTrades().get(recipe.getLevel()).add(
                        new RandomTradeBuilder(64, 25, 0.05f)
                                .setPrice(recipe.getItem1(), recipe.price1Min().get(), recipe.price1Max().get())
                                .setPrice2(recipe.getItem2(), recipe.price2Min().get(), recipe.price2Max().get())
                                .setForSale(recipe.getOutput(), recipe.outputMin().get(), recipe.outputMax().get())
                                .build());
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void modifyStructureSpawnList(StructureSpawnListGatherEvent event){
//        if(event.getStructure() == StructureRegister.CASTLE_IN_THE_SKY.get()){
//            // No mob should spawn here
//            // TODO: add misc spawn like fish, iron golems, etc..
//            List<MobSpawnInfo.Spawners> spawners = event.getEntitySpawns(EntityClassification.MONSTER);
//            for(MobSpawnInfo.Spawners spawner: spawners){
//                event.removeEntitySpawn(EntityClassification.MONSTER, spawner);
//            }
//        }
    }

    @SubscribeEvent
    public void onBlockBreak(PlayerEvent.BreakSpeed event){
        Player playerEntity = event.getPlayer();
        if(playerEntity.hasEffect(EffectRegister.SACRED_CASTLE_EFFECT.get()) && !playerEntity.isCreative()){
            event.setCanceled(true);
            if(playerEntity.level.isClientSide()){
                ClientHandlerClass.showSacredCastleInfoBreak();
            }
        }
    }

    @SubscribeEvent
    public void onBlockPlaced(BlockEvent.EntityPlaceEvent event){
        Entity entity = event.getEntity();
        if(entity instanceof Player && ((Player)entity).hasEffect(EffectRegister.SACRED_CASTLE_EFFECT.get()) && !((Player) entity).isCreative()){
            event.setCanceled(true);
            if(entity instanceof ServerPlayer){
                Channel.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) entity),
                        new ServerToClientInfoPacket(new TranslatableComponent(String.format("info.%s.sacred_castle_effect.place", CastleInTheSky.MOD_ID)).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD)));
            }
        }
    }

    @SubscribeEvent
    public void onMobDrop(LivingDropsEvent event){
        DamageSource damageSource = event.getSource();
        if(damageSource instanceof EntityDamageSource){
            Entity killer = damageSource.getEntity();
            if(killer instanceof LivingEntity && ((LivingEntity) killer).hasEffect(EffectRegister.SACRED_CASTLE_EFFECT.get())){
                LivingEntity dropper = event.getEntityLiving();
                if(dropper.getRandom().nextDouble()< ConfigCommon.YELLOW_KEY_DROP_RATE.get()){
                    event.getDrops().add(new ItemEntity(dropper.level, dropper.position().x, dropper.position().y, dropper.position().z, new ItemStack(ItemsRegister.YELLOW_KEY.get())));
                }
                if(dropper.getRandom().nextDouble()<ConfigCommon.BLUE_KEY_DROP_RATE.get()){
                    event.getDrops().add(new ItemEntity(dropper.level, dropper.position().x, dropper.position().y, dropper.position().z, new ItemStack(ItemsRegister.BLUE_KEY.get())));
                }
                if(dropper.getRandom().nextDouble()<ConfigCommon.RED_KEY_DROP_RATE.get()){
                    event.getDrops().add(new ItemEntity(dropper.level, dropper.position().x, dropper.position().y, dropper.position().z, new ItemStack(ItemsRegister.RED_KEY.get())));
                }
            }
        }
    }
}
