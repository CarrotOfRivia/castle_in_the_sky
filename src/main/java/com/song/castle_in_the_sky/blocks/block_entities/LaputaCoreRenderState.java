package com.song.castle_in_the_sky.blocks.block_entities;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;

public class LaputaCoreRenderState extends BlockEntityRenderState {
    public final ItemStackRenderState orb = new ItemStackRenderState();
    public boolean active;
    public boolean destroying;
    public float time;
}
