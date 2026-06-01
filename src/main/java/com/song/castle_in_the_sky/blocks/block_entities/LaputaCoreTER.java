package com.song.castle_in_the_sky.blocks.block_entities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.song.castle_in_the_sky.items.ItemsRegister;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.jspecify.annotations.Nullable;

public class LaputaCoreTER implements BlockEntityRenderer<LaputaCoreBE, LaputaCoreTER.RenderState> {
    private static final int NO_OUTLINE = 0;
    private final ItemModelResolver itemModelResolver;
    private float degrees;

    public LaputaCoreTER(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public RenderState createRenderState() {
        return new RenderState();
    }

    @Override
    public void extractRenderState(LaputaCoreBE laputaCoreBE, RenderState renderState, float partialTick, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(laputaCoreBE, renderState, partialTick, cameraPosition, breakProgress);
        renderState.active = laputaCoreBE.isActive();
        renderState.destroying = laputaCoreBE.isDestroying();
        renderState.destroyProgress = laputaCoreBE.getDestroyProgress();
        renderState.gameTime = laputaCoreBE.getLevel() == null ? partialTick : laputaCoreBE.getLevel().getGameTime() + partialTick;
        renderState.activatedDelta = Vec3.atLowerCornerOf(laputaCoreBE.getBlockPos()).vectorTo(laputaCoreBE.getActivatedInitPos());
        itemModelResolver.updateForTopItem(renderState.coreOrb, new ItemStack(ItemsRegister.LAPUTA_CORE_ORB.get()), ItemDisplayContext.GROUND, laputaCoreBE.getLevel(), null, 0);
        itemModelResolver.updateForTopItem(renderState.levitationStone, new ItemStack(ItemsRegister.LEVITATION_STONE.get()), ItemDisplayContext.FIXED, laputaCoreBE.getLevel(), null, 1);
    }

    @Override
    public void submit(RenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
        if (renderState.destroying) {
            poseStack.pushPose();
            poseStack.translate(0.5D, 1.6D, 0.5D);
            poseStack.mulPose(new Quaternionf().rotateY(degrees / 2));
            degrees += 30;
            renderState.coreOrb.submit(poseStack, nodeCollector, renderState.lightCoords, OverlayTexture.NO_OVERLAY, NO_OUTLINE);
            poseStack.popPose();

            double partial = 1.0D * (LaputaCoreBE.ANIMATION_TIME - renderState.destroyProgress) / LaputaCoreBE.ANIMATION_TIME;
            if (partial > 0) {
                poseStack.pushPose();
                poseStack.translate(0.5D, 1.5D, 0.5D);
                Vec3 delta = renderState.activatedDelta.multiply(partial, partial, partial);
                poseStack.translate(delta.x(), delta.y(), delta.z());
                poseStack.mulPose(new Quaternionf().rotateY(degrees / 2));
                renderState.levitationStone.submit(poseStack, nodeCollector, renderState.lightCoords, OverlayTexture.NO_OVERLAY, NO_OUTLINE);
                poseStack.popPose();
            }
        } else if (renderState.active) {
            poseStack.pushPose();
            poseStack.translate(0.5D, (Math.sin(Math.PI * renderState.gameTime / 16) / 4) + 1.6D, 0.5D);
            poseStack.mulPose(new Quaternionf().rotateY(degrees++ / 2));
            renderState.coreOrb.submit(poseStack, nodeCollector, renderState.lightCoords, OverlayTexture.NO_OVERLAY, NO_OUTLINE);
            poseStack.popPose();
        } else {
            poseStack.pushPose();
            poseStack.translate(0.5D, 1.5D, 0.5D);
            renderState.coreOrb.submit(poseStack, nodeCollector, renderState.lightCoords, OverlayTexture.NO_OVERLAY, NO_OUTLINE);
            poseStack.popPose();
        }
    }

    public static class RenderState extends BlockEntityRenderState {
        private final ItemStackRenderState coreOrb = new ItemStackRenderState();
        private final ItemStackRenderState levitationStone = new ItemStackRenderState();
        private boolean active;
        private boolean destroying;
        private int destroyProgress;
        private float gameTime;
        private Vec3 activatedDelta = Vec3.ZERO;
    }
}
