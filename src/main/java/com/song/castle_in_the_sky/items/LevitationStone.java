package com.song.castle_in_the_sky.items;

import com.song.castle_in_the_sky.CastleInTheSky;
import com.song.castle_in_the_sky.config.ConfigCommon;
import com.song.castle_in_the_sky.effects.EffectRegister;
import com.song.castle_in_the_sky.structures.StructureRegister;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

import java.util.List;

public class LevitationStone extends Item {
    public LevitationStone(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public void inventoryTick(ItemStack itemStack, Level world, Entity entity, int slot, boolean selected) {
        boolean isSelected = selected || entity instanceof Player player && (player.getMainHandItem() == itemStack || player.getOffhandItem() == itemStack);
        if(isSelected && isActive(itemStack)){
            if(world.getGameTime() % 40 == 0){
                if(entity instanceof LivingEntity){
                    if(((LivingEntity) entity).hasEffect(EffectRegister.SACRED_CASTLE_EFFECT)){
                        ((LivingEntity) entity).addEffect(new MobEffectInstance(MobEffects.LEVITATION, 100));
                    }
                    else {
                        ((LivingEntity) entity).addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 100));
                    }
                }
            }
        }
        super.inventoryTick(itemStack, world, entity, slot, selected);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player playerEntity, InteractionHand hand) {
        ItemStack itemStack = playerEntity.getItemInHand(hand);
        if(!world.isClientSide()){
            int useDistance = (int) (ConfigCommon.LEVITATION_STONE_USE_PERCENT.get()*ConfigCommon.CASTLE_SPAWN_PROOF.get());
            if(playerEntity.blockPosition().closerThan(new Vec3i(0, 0, 0), useDistance)){
                playerEntity.displayClientMessage(Component.translatable("info."+CastleInTheSky.MOD_ID+".too_close_to_spawn", ConfigCommon.CASTLE_SPAWN_PROOF.get(),  useDistance).withStyle(ChatFormatting.RED, ChatFormatting.BOLD), false);
                return super.use(world, playerEntity, hand);
            }

            CompoundTag root = getCustomData(itemStack);
            CompoundTag nbt = root.getCompound("castle_in_the_sky");
            nbt.putBoolean("active", !nbt.getBoolean("active"));
            root.put("castle_in_the_sky", nbt);
            setCustomData(itemStack, root);

            if(isActive(itemStack)){
                BlockPos blockPos = ((ServerLevel) world).findNearestMapStructure(StructureRegister.CASTLE_IN_THE_SKY_LOCATED, playerEntity.blockPosition(), 100, false);
                if(blockPos!=null){
                    root = getCustomData(itemStack);
                    CompoundTag nbt1 = root.getCompound("targetLaputa");
                    nbt1.putInt("posX", blockPos.getX()+72);
                    nbt1.putInt("posY", ConfigCommon.CASTLE_HEIGHT.get()+72);
                    nbt1.putInt("posZ", blockPos.getZ()+72);
                    root.put("targetLaputa", nbt1);
                    setCustomData(itemStack, root);
                }
            }
        }
        return super.use(world, playerEntity, hand);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, List<Component> tooltip, TooltipFlag iTooltipFlag) {
        super.appendHoverText(itemStack, context, tooltip, iTooltipFlag);

        tooltip.add(Component.translatable("tooltip."+ CastleInTheSky.MOD_ID+".levitation_stone.line1").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip."+ CastleInTheSky.MOD_ID+".levitation_stone.line2").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip."+ CastleInTheSky.MOD_ID+".levitation_stone.line3").withStyle(ChatFormatting.GRAY));

        String s;
        if (isActive(itemStack)){
            s = "ON";
        }
        else {
            s = "OFF";
        }
        tooltip.add(Component.literal(s).withStyle(ChatFormatting.GOLD));
    }

    public boolean isActive(ItemStack itemStack){
        CompoundTag nbt = getSubTag(itemStack, "castle_in_the_sky");
        return nbt.getBoolean("active");
    }

    public void spawnGuidingParticles(ItemStack itemStack, Level world, Entity entity) {
        if (!world.isClientSide() || !world.dimension().location().toString().equals("minecraft:overworld")) {
            return;
        }

        CompoundTag nbt = getSubTag(itemStack, "targetLaputa");
        if (nbt.isEmpty()) {
            return;
        }

        int posX = nbt.getInt("posX");
        int posY = nbt.getInt("posY");
        int posZ = nbt.getInt("posZ");
        BlockPos entityPos = entity.blockPosition();
        double dist = Math.sqrt(entityPos.distSqr(new Vec3i(posX, posY, posZ)));
        if (dist <= 0.0001D) {
            return;
        }

        double dx = (posX - entityPos.getX()) / dist;
        double dy = (posY - entityPos.getY() - 2) / dist;
        double dz = (posZ - entityPos.getZ()) / dist;
        for (int i = 2; i < 50; i++) {
            world.addParticle(ParticleTypes.CLOUD, i * dx + entityPos.getX(), i * dy + entityPos.getY(), i * dz + entityPos.getZ(), 0, 0, 0);
        }
    }

    private static CompoundTag getCustomData(ItemStack itemStack) {
        return itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
    }

    private static CompoundTag getSubTag(ItemStack itemStack, String key) {
        return getCustomData(itemStack).getCompound(key);
    }

    private static void setCustomData(ItemStack itemStack, CompoundTag tag) {
        itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }
}
