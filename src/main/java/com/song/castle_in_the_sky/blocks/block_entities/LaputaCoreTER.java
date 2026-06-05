package com.song.castle_in_the_sky.blocks.block_entities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.song.castle_in_the_sky.items.ItemsRegister;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

import java.util.Objects;

public class LaputaCoreTER implements BlockEntityRenderer<LaputaCoreBE> {
    private float degrees;

    public LaputaCoreTER(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(LaputaCoreBE laputaCoreBE, float partialTicks, PoseStack poseStack, MultiBufferSource multiBufferSource, int combinedLightIn, int combinedOverlayIn) {
        if (laputaCoreBE.isDestroying()) {
            poseStack.pushPose();
            poseStack.translate(0.5D, 1.5D, 0.5D);
            Objects.requireNonNull(laputaCoreBE.getLevel());
            poseStack.translate(0D, 0.1D, 0D);
            poseStack.mulPose(new Quaternionf().rotateY(degrees / 2));
            degrees += 30;
            renderItem(new ItemStack(ItemsRegister.LAPUTA_CORE_ORB.get()), laputaCoreBE.getLevel(), poseStack, multiBufferSource, combinedLightIn);
            poseStack.popPose();

            double partial = 1.0D * (LaputaCoreBE.ANIMATION_TIME - laputaCoreBE.getDestroyProgress()) / LaputaCoreBE.ANIMATION_TIME;
            if (partial > 0) {
                poseStack.pushPose();
                poseStack.translate(0.5D, 1.5D, 0.5D);
                Vec3 basePos = Vec3.atLowerCornerOf(laputaCoreBE.getBlockPos());
                Vec3 delta = basePos.vectorTo(laputaCoreBE.getActivatedInitPos()).multiply(partial, partial, partial);
                poseStack.translate(delta.x(), delta.y(), delta.z());
                poseStack.mulPose(new Quaternionf().rotateY(degrees / 2));
                renderItem(new ItemStack(ItemsRegister.LEVITATION_STONE.get()), laputaCoreBE.getLevel(), poseStack, multiBufferSource, combinedLightIn);
                poseStack.popPose();
            }
        } else if (laputaCoreBE.isActive()) {
            poseStack.pushPose();
            poseStack.translate(0.5D, 1.5D, 0.5D);
            float currentTime = Objects.requireNonNull(laputaCoreBE.getLevel()).getGameTime() + partialTicks;
            poseStack.translate(0D, (Math.sin(Math.PI * currentTime / 16) / 4) + 0.1D, 0D);
            poseStack.mulPose(new Quaternionf().rotateY(degrees++ / 2));
            renderItem(new ItemStack(ItemsRegister.LAPUTA_CORE_ORB.get()), laputaCoreBE.getLevel(), poseStack, multiBufferSource, combinedLightIn);
            poseStack.popPose();
        } else {
            poseStack.pushPose();
            poseStack.translate(0.5D, 1.5D, 0.5D);
            renderItem(new ItemStack(ItemsRegister.LAPUTA_CORE_ORB.get()), laputaCoreBE.getLevel(), poseStack, multiBufferSource, combinedLightIn);
            poseStack.popPose();
        }
    }

    private void renderItem(ItemStack stack, Level level, PoseStack poseStack, MultiBufferSource buffer, int combinedLight) {
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.FIXED, combinedLight, OverlayTexture.NO_OVERLAY, poseStack, buffer, level, 0);
    }
}
