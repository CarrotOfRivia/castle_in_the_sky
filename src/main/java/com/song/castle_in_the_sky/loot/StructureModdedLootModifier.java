package com.song.castle_in_the_sky.loot;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;

import javax.annotation.Nonnull;
import java.util.List;

public class StructureModdedLootModifier extends LootModifier {
    protected StructureModdedLootModifier(ILootCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Nonnull
    @Override
    protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
        return generatedLoot;
    }
    public static class Serializer extends GlobalLootModifierSerializer<StructureModdedLootModifier> {
        @Override
        public StructureModdedLootModifier read(ResourceLocation location, JsonObject object, ILootCondition[] conditions) {
            return new StructureModdedLootModifier(conditions);
        }

        @Override
        public JsonObject write(StructureModdedLootModifier instance) {
            return this.makeConditions(instance.conditions);
        }
    }

}
