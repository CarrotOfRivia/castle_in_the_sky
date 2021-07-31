package com.song.castle_in_the_sky.features;

import com.mojang.serialization.Codec;
import com.song.castle_in_the_sky.CastleInTheSky;
import com.song.castle_in_the_sky.config.ConfigCommon;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.structures.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class CastleStructure extends StructureFeature<NoneFeatureConfiguration> {
    public CastleStructure(Codec<NoneFeatureConfiguration> noFeatureConfigCodec) {
        super(noFeatureConfigCodec);
    }

    @Override
    public GenerationStep.Decoration step() {
        return GenerationStep.Decoration.SURFACE_STRUCTURES;
    }

    @Override
    public List<MobSpawnSettings.SpawnerData> getDefaultSpawnList() {
        return Collections.singletonList(new MobSpawnSettings.SpawnerData(EntityType.ZOMBIE, 1, 1, 1));
    }

    @Override
    public StructureFeature.StructureStartFactory<NoneFeatureConfiguration> getStartFactory() {
        return Start::new;
    }

    public static class Start extends StructureStart<NoneFeatureConfiguration> {

        public Start(StructureFeature<NoneFeatureConfiguration> noFeatureConfigStructure, ChunkPos chunkPos, int references, long p_163598_) {
            super(noFeatureConfigStructure,chunkPos, references, p_163598_);
        }

        @Override
        public void generatePieces(RegistryAccess registryAccess, ChunkGenerator chunkGenerator, StructureManager templateManagerIn, ChunkPos chunkPos, Biome biomeIn, NoneFeatureConfiguration config, LevelHeightAccessor levelHeightAccessor) {
            int i = chunkPos.x * 16;
            int j = chunkPos.z * 16;

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
                        JigsawPlacement.addPieces(
                                registryAccess,
                                new JigsawConfiguration(() -> registryAccess.registryOrThrow(Registry.TEMPLATE_POOL_REGISTRY)
                                        .get(new ResourceLocation(CastleInTheSky.MOD_ID, String.format("castle_in_the_sky/laputa%d%d%d", finalShift1, finalShiftY, finalShift2))),
                                        0),
                                PoolElementStructurePiece::new,
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

            this.getBoundingBox();

        }
    }

    public static class FakeRandom extends Random{
        @Override
        public int nextInt(int bound) {
            return 0;
        }
    }

}
