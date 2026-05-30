package com.song.castle_in_the_sky.blocks.block_entities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.song.castle_in_the_sky.blocks.LaputaCore;
import com.song.castle_in_the_sky.items.ItemsRegister;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class LaputaCoreTER implements BlockEntityRenderer<LaputaCoreBE, LaputaCoreRenderState> {
    public LaputaCoreTER(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public LaputaCoreRenderState createRenderState() {
        return new LaputaCoreRenderState();
    }

    @Override
    public void extractRenderState(LaputaCoreBE blockEntity, LaputaCoreRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        state.active = blockEntity.getBlockState().getValue(LaputaCore.POWERED);
        state.destroying = blockEntity.getBlockState().getValue(LaputaCore.DESTROYING);
        state.time = blockEntity.getLevel() == null ? partialTicks : blockEntity.getLevel().getGameTime() + partialTicks;
        Minecraft.getInstance().getItemModelResolver().updateForTopItem(
                state.orb,
                new ItemStack(ItemsRegister.LAPUTA_CORE_ORB.get()),
                ItemDisplayContext.FIXED,
                blockEntity.getLevel(),
                null,
                (int) blockEntity.getBlockPos().asLong()
        );
    }

    @Override
    public void submit(LaputaCoreRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.translate(0.5F, 1.5F, 0.5F);
        if (state.active || state.destroying) {
            poseStack.translate(0.0F, (float) Math.sin(Math.PI * state.time / 16.0F) / 4.0F + 0.1F, 0.0F);
            poseStack.mulPose(Axis.YP.rotationDegrees(state.time * 9.0F));
        }
        state.orb.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        poseStack.popPose();
    }

    @Override
    public AABB getRenderBoundingBox(LaputaCoreBE blockEntity) {
        var pos = blockEntity.getBlockPos();
        return new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1.0, pos.getY() + 2.0, pos.getZ() + 1.0);
    }
}
