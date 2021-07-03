package com.song.castle_in_the_sky.blocks.tile_entities;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.song.castle_in_the_sky.items.ItemsRegister;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.vector.Vector3f;

import java.util.Objects;

public class LaputaCoreTER extends TileEntityRenderer<LaputaCoreTE> {
    private float degrees;

    public LaputaCoreTER(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(LaputaCoreTE tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        matrixStackIn.pushPose();
        matrixStackIn.translate(0.5D, 1.5D, 0.5D);
        float currentTime = Objects.requireNonNull(tileEntityIn.getLevel()).getGameTime() + partialTicks;
        matrixStackIn.translate(0D, (Math.sin(Math.PI * currentTime / 16) / 4) + 0.1D, 0D);
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(degrees++ / 2));
        renderItem(new ItemStack(ItemsRegister.LAPUTA_CORE_ORB.get()), partialTicks, matrixStackIn, bufferIn, combinedLightIn);
        matrixStackIn.popPose();
    }

    private void renderItem(ItemStack stack, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn) {
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemCameraTransforms.TransformType.FIXED, combinedLightIn, OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn);
    }
}
