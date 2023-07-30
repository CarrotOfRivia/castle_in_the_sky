package com.song.castle_in_the_sky.events;

import com.song.castle_in_the_sky.items.ItemsRegister;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import static com.song.castle_in_the_sky.CastleInTheSky.MOD_ID;

public class ModEvents {
    @SubscribeEvent
    public void buildContents(final CreativeModeTabEvent.Register event) {
        event.registerCreativeModeTab(new ResourceLocation(MOD_ID, "item_group"), builder ->
                // Set name of tab to display
                builder.title(Component.translatable("item_group." + MOD_ID + ".item_group"))
                        // Set icon of creative tab
                        .icon(() -> new ItemStack(ItemsRegister.LEVITATION_STONE.get()))
                        // Add default items to tab
                        .displayItems((params, output) -> {
                            output.accept(ItemsRegister.LEVITATION_STONE.get());
                            output.accept(ItemsRegister.RED_DOOR.get());
                            output.accept(ItemsRegister.BLUE_DOOR.get());
                            output.accept(ItemsRegister.YELLOW_DOOR.get());

                            output.accept(ItemsRegister.LAPUTA_MINIATURE.get());
                            output.accept(ItemsRegister.FAKE_BEACON.get());
                            output.accept(ItemsRegister.RED_KEY.get());
                            output.accept(ItemsRegister.BLUE_KEY.get());
                            output.accept(ItemsRegister.YELLOW_KEY.get());

                            output.accept(ItemsRegister.LAPUTA_CORE.get());
                            output.accept(ItemsRegister.LAPUTA_CORE_ORB.get());
                        })
        );
    }
}
