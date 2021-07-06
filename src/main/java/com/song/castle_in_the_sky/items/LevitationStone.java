package com.song.castle_in_the_sky.items;

import com.song.castle_in_the_sky.CastleInTheSky;
import com.song.castle_in_the_sky.config.ConfigCommon;
import com.song.castle_in_the_sky.effects.EffectRegister;
import com.song.castle_in_the_sky.features.CastleStructure;
import com.song.castle_in_the_sky.features.StructureRegister;
import net.minecraft.block.BeaconBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.List;

public class LevitationStone extends Item {
    public LevitationStone() {
        super(new Item.Properties().tab(CastleInTheSky.ITEM_GROUP).stacksTo(1));
    }

    @Override
    public void inventoryTick(ItemStack itemStack, World world, Entity entity, int itemSlot, boolean isSelected) {
        if(isSelected && isActive(itemStack)){
            if(!world.isClientSide() && world instanceof ServerWorld && world.getGameTime() % 40 == 0){
                if(entity instanceof LivingEntity){
                    if(((LivingEntity) entity).hasEffect(EffectRegister.SACRED_CASTLE_EFFECT.get())){
                        ((LivingEntity) entity).addEffect(new EffectInstance(Effects.LEVITATION, 100));
                    }
                    else {
                        ((LivingEntity) entity).addEffect(new EffectInstance(Effects.SLOW_FALLING, 100));
                    }
                }
            }
            if(world.isClientSide() && world.dimension().location().toString().equals("minecraft:overworld")){
                CompoundNBT nbt = itemStack.getTagElement("targetLaputa");
                if(nbt!=null){
                    int posX = nbt.getInt("posX");
                    int posY = nbt.getInt("posY");
                    int posZ = nbt.getInt("posZ");
                    BlockPos entityPos = entity.blockPosition();
                    double dist = Math.sqrt(entityPos.distSqr(posX, posY, posZ, false));
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
    public ActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
        if(!world.isClientSide()){
            ItemStack itemStack = playerEntity.getItemInHand(hand);
            CompoundNBT nbt = itemStack.getOrCreateTagElement("castle_in_the_sky");
            nbt.putBoolean("active", !nbt.getBoolean("active"));

            if(isActive(itemStack)){
                BlockPos blockpos = ((ServerWorld) world).findNearestMapFeature(StructureRegister.CASTLE_IN_THE_SKY.get(), playerEntity.blockPosition(), 100, false);
                if(blockpos!=null){
                    CompoundNBT nbt1 = itemStack.getOrCreateTagElement("targetLaputa");
                    nbt1.putInt("posX", blockpos.getX()+72);
                    nbt1.putInt("posY", ConfigCommon.CASTLE_HEIGHT.get()+72);
                    nbt1.putInt("posZ", blockpos.getZ()+72);
                }
            }
        }
        return super.use(world, playerEntity, hand);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable World p_77624_2_, List<ITextComponent> iTextComponents, ITooltipFlag iTooltipFlag) {
        super.appendHoverText(itemStack, p_77624_2_, iTextComponents, iTooltipFlag);

        iTextComponents.add(new TranslationTextComponent("tooltip."+ CastleInTheSky.MOD_ID+".levitation_stone.line1").withStyle(TextFormatting.GRAY));
        iTextComponents.add(new TranslationTextComponent("tooltip."+ CastleInTheSky.MOD_ID+".levitation_stone.line2").withStyle(TextFormatting.GRAY));
        iTextComponents.add(new TranslationTextComponent("tooltip."+ CastleInTheSky.MOD_ID+".levitation_stone.line3").withStyle(TextFormatting.GRAY));

        String s;
        if (isActive(itemStack)){
            s = "ON";
        }
        else {
            s = "OFF";
        }
        iTextComponents.add(new StringTextComponent(s).withStyle(TextFormatting.GOLD));
    }

    public boolean isActive(ItemStack itemStack){
        CompoundNBT nbt = itemStack.getOrCreateTagElement("castle_in_the_sky");
        return nbt.getBoolean("active");
    }
}
