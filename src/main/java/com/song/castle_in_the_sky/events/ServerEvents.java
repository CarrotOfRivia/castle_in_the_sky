package com.song.castle_in_the_sky.events;

import com.song.castle_in_the_sky.CastleInTheSky;
import com.song.castle_in_the_sky.config.ConfigCommon;
import com.song.castle_in_the_sky.effects.EffectRegister;
import com.song.castle_in_the_sky.features.StructureFeatureRegister;
import com.song.castle_in_the_sky.features.StructureRegister;
import com.song.castle_in_the_sky.items.ItemsRegister;
import com.song.castle_in_the_sky.network.Channel;
import com.song.castle_in_the_sky.network.ClientHandlerClass;
import com.song.castle_in_the_sky.network.ServerToClientInfoPacket;
import com.song.castle_in_the_sky.utils.MyTradingRecipe;
import com.song.castle_in_the_sky.utils.RandomTradeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.NetworkManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.StructureSpawnListGatherEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class ServerEvents {

    @SubscribeEvent
    public void onBiomeLoading(BiomeLoadingEvent event){
        if(event.getCategory() == Biome.Category.OCEAN){
            event.getGeneration().addStructureStart(StructureFeatureRegister.CONFIGURED_CASTLE_IN_THE_SKY);
        }
    }

    @SubscribeEvent
    public void onVillageTradeRegister(VillagerTradesEvent event){
        for (MyTradingRecipe recipe: ConfigCommon.MY_TRADING_RECIPES){
            if(recipe.getItem1()!=null || recipe.getItem2() != null && Objects.requireNonNull(event.getType().getRegistryName()).toString().equals(recipe.getStringProfession())){
                event.getTrades().get((int)(recipe.level.get())).add(
                        new RandomTradeBuilder(64, 25, 0.05f)
                                .setPrice(recipe.getItem1(), recipe.price1Min.get(), recipe.price1Max.get())
                                .setPrice2(recipe.getItem2(), recipe.price2Min.get(), recipe.price2Max.get())
                                .setForSale(recipe.getOutput(), recipe.outputMin.get(), recipe.outputMax.get())
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
        PlayerEntity playerEntity = event.getPlayer();
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
        if(entity instanceof PlayerEntity && ((PlayerEntity)entity).hasEffect(EffectRegister.SACRED_CASTLE_EFFECT.get()) && !((PlayerEntity) entity).isCreative()){
            event.setCanceled(true);
            if(entity instanceof ServerPlayerEntity){
                Channel.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) entity),
                        new ServerToClientInfoPacket(new TranslationTextComponent(String.format("info.%s.sacred_castle_effect.place", CastleInTheSky.MOD_ID)).withStyle(TextFormatting.RED).withStyle(TextFormatting.BOLD)));
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
