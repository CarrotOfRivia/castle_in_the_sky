package com.song.castle_in_the_sky.client;

import com.song.castle_in_the_sky.CastleInTheSky;
import com.song.castle_in_the_sky.items.LevitationStone;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = CastleInTheSky.MOD_ID, value = Dist.CLIENT)
public class ClientGameEvents {
    @SubscribeEvent
    public static void clientTick(ClientTickEvent.Post event) {
        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        spawnGuidingParticles(player.getMainHandItem(), player);
        spawnGuidingParticles(player.getOffhandItem(), player);
    }

    private static void spawnGuidingParticles(ItemStack itemStack, Player player) {
        if (itemStack.getItem() instanceof LevitationStone levitationStone && levitationStone.isActive(itemStack)) {
            levitationStone.spawnGuidingParticles(itemStack, player.level(), player);
        }
    }
}
