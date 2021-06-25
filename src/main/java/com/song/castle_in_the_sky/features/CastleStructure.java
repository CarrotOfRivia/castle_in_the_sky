package com.song.castle_in_the_sky.features;

import com.mojang.serialization.Codec;
import com.song.castle_in_the_sky.CastleInTheSky;
import com.song.castle_in_the_sky.config.ConfigCommon;
import com.song.castle_in_the_sky.config.ConfigServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.jigsaw.JigsawManager;
import net.minecraft.world.gen.feature.structure.AbstractVillagePiece;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.structure.VillageConfig;
import net.minecraft.world.gen.feature.template.TemplateManager;
import org.apache.logging.log4j.Level;

import java.util.Random;

public class CastleStructure extends Structure<NoFeatureConfig> {
    public CastleStructure(Codec<NoFeatureConfig> noFeatureConfigCodec) {
        super(noFeatureConfigCodec);
    }

    @Override
    public GenerationStage.Decoration step() {
        return GenerationStage.Decoration.SURFACE_STRUCTURES;
    }

    @Override
    public IStartFactory<NoFeatureConfig> getStartFactory() {
        return Start::new;
    }

    public static class Start extends StructureStart<NoFeatureConfig>{

        public Start(Structure<NoFeatureConfig> noFeatureConfigStructure, int p_i225876_2_, int p_i225876_3_, MutableBoundingBox mutableBoundingBox, int p_i225876_5_, long p_i225876_6_) {
            super(noFeatureConfigStructure, p_i225876_2_, p_i225876_3_, mutableBoundingBox, p_i225876_5_, p_i225876_6_);
        }

        @Override
        public void generatePieces(DynamicRegistries dynamicRegistryManager, ChunkGenerator chunkGenerator, TemplateManager templateManagerIn, int chunkX, int chunkZ, Biome biomeIn, NoFeatureConfig config) {
            int i = chunkX * 16;
            int j = chunkZ * 16;

            if(i*i+j*j < ConfigCommon.CASTLE_SPAWN_PROOF.get()*ConfigCommon.CASTLE_SPAWN_PROOF.get()){
                return;
            }

            BlockPos blockpos = new BlockPos(i, ConfigCommon.CASTLE_HEIGHT.get(), j);
            FakeRandom fakeRandom = new FakeRandom();

            for (int shift1=0; shift1<3; shift1++){
                for (int shift2 =0; shift2<3; shift2++){
                    for (int shiftY =0; shiftY<3; shiftY++){
                        // All a structure has to do is call this method to turn it into a jigsaw based structure!
                        int finalShift1 = shift1;
                        int finalShift2 = shift2;
                        int finalShiftY = shiftY;
                        JigsawManager.addPieces(
                                dynamicRegistryManager,
                                new VillageConfig(() -> dynamicRegistryManager.registryOrThrow(Registry.TEMPLATE_POOL_REGISTRY)
                                        .get(new ResourceLocation(CastleInTheSky.MOD_ID, String.format("castle_in_the_sky/laputa%d%d%d", finalShift1, finalShiftY, finalShift2))),
                                        0),
                                AbstractVillagePiece::new,
                                chunkGenerator,
                                templateManagerIn,
                                blockpos.offset(shift2*48, shiftY*48, shift1*48),
                                this.pieces,
                                fakeRandom,
                                false,
                                false);
                    }
                }
            }

            this.calculateBoundingBox();

        }
    }

    public static class FakeRandom extends Random{
        @Override
        public int nextInt(int bound) {
            return 0;
        }
    }

}
