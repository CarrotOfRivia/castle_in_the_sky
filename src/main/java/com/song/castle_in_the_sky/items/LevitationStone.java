package com.song.castle_in_the_sky.items;

import com.song.castle_in_the_sky.CastleInTheSky;
import com.song.castle_in_the_sky.config.ConfigCommon;
import com.song.castle_in_the_sky.effects.EffectRegister;
import com.song.castle_in_the_sky.structures.StructureRegister;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class LevitationStone extends Item {
    public LevitationStone() {
        super(new Item.Properties().tab(CastleInTheSky.ITEM_GROUP).stacksTo(1));
    }

    @Override
    public void inventoryTick(ItemStack itemStack, Level world, Entity entity, int itemSlot, boolean isSelected) {
        if(isSelected && isActive(itemStack)){
            if(!world.isClientSide() && world instanceof ServerLevel && world.getGameTime() % 40 == 0){
                if(entity instanceof LivingEntity){
                    if(((LivingEntity) entity).hasEffect(EffectRegister.SACRED_CASTLE_EFFECT.get())){
                        ((LivingEntity) entity).addEffect(new MobEffectInstance(MobEffects.LEVITATION, 100));
                    }
                    else {
                        ((LivingEntity) entity).addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 100));
                    }
                }
            }
            if(world.isClientSide() && world.dimension().location().toString().equals("minecraft:overworld")){
                CompoundTag nbt = itemStack.getTagElement("targetLaputa");
                if(nbt!=null){
                    int posX = nbt.getInt("posX");
                    int posY = nbt.getInt("posY");
                    int posZ = nbt.getInt("posZ");
                    BlockPos entityPos = entity.blockPosition();
                    double dist = Math.sqrt(entityPos.distSqr(new Vec3i(posX, posY, posZ)));
                    double dx = (posX - entityPos.getX())/dist;
                    double dy = (posY - entityPos.getY()-2)/dist;
                    double dz = (posZ - entityPos.getZ())/dist;
                    for(int i = 2; i<50; i++){
                        world.addParticle(ParticleTypes.CLOUD, i*dx+ entityPos.getX(), i*dy+ entityPos.getY(), i*dz+ entityPos.getZ(),
                                0, 0, 0);
                    }
                }
            }
        }
        super.inventoryTick(itemStack, world, entity, itemSlot, isSelected);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player playerEntity, InteractionHand hand) {
        if(!world.isClientSide()){
            int useDistance = (int) (ConfigCommon.LEVITATION_STONE_USE_PERCENT.get()*ConfigCommon.CASTLE_SPAWN_PROOF.get());
            if(playerEntity.blockPosition().closerThan(new Vec3i(0, 0, 0), useDistance)){
                playerEntity.sendSystemMessage(Component.translatable("info."+CastleInTheSky.MOD_ID+".too_close_to_spawn", ConfigCommon.CASTLE_SPAWN_PROOF.get(),  useDistance).withStyle(ChatFormatting.RED, ChatFormatting.BOLD));
                return super.use(world, playerEntity, hand);
            }

            ItemStack itemStack = playerEntity.getItemInHand(hand);
            CompoundTag nbt = itemStack.getOrCreateTagElement("castle_in_the_sky");
            nbt.putBoolean("active", !nbt.getBoolean("active"));

            if(isActive(itemStack)){
                BlockPos blockpos = ((ServerLevel) world).findNearestMapStructure(StructureRegister.CASTLE_IN_THE_SKY_LOCATED, playerEntity.blockPosition(), 100, false);
                if(blockpos!=null){
                    CompoundTag nbt1 = itemStack.getOrCreateTagElement("targetLaputa");
                    nbt1.putInt("posX", blockpos.getX()+72);
                    nbt1.putInt("posY", ConfigCommon.CASTLE_HEIGHT.get()+72);
                    nbt1.putInt("posZ", blockpos.getZ()+72);
                }
            }
        }
        return super.use(world, playerEntity, hand);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level p_77624_2_, List<Component> iTextComponents, TooltipFlag iTooltipFlag) {
        super.appendHoverText(itemStack, p_77624_2_, iTextComponents, iTooltipFlag);

        iTextComponents.add(Component.translatable("tooltip."+ CastleInTheSky.MOD_ID+".levitation_stone.line1").withStyle(ChatFormatting.GRAY));
        iTextComponents.add(Component.translatable("tooltip."+ CastleInTheSky.MOD_ID+".levitation_stone.line2").withStyle(ChatFormatting.GRAY));
        iTextComponents.add(Component.translatable("tooltip."+ CastleInTheSky.MOD_ID+".levitation_stone.line3").withStyle(ChatFormatting.GRAY));

        String s;
        if (isActive(itemStack)){
            s = "ON";
        }
        else {
            s = "OFF";
        }
        iTextComponents.add(Component.literal(s).withStyle(ChatFormatting.GOLD));
    }

    public boolean isActive(ItemStack itemStack){
        CompoundTag nbt = itemStack.getOrCreateTagElement("castle_in_the_sky");
        return nbt.getBoolean("active");
    }
}
