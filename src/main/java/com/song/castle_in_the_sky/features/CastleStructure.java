package com.song.castle_in_the_sky.features;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.song.castle_in_the_sky.CastleInTheSky;
import com.song.castle_in_the_sky.config.ConfigCommon;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraft.world.level.levelgen.feature.structures.StructurePoolElement;
import net.minecraft.world.level.levelgen.feature.structures.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.PostPlacementProcessor;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class CastleStructure extends StructureFeature<JigsawConfiguration> {
    public static final StructureTemplatePool START = Pools.register(new StructureTemplatePool(new ResourceLocation(CastleInTheSky.MOD_ID+":castle_in_the_sky"), new ResourceLocation("empty"),
            ImmutableList.of(Pair.of(StructurePoolElement.legacy(CastleInTheSky.MOD_ID+":laputa000.nbt"), 1)), StructureTemplatePool.Projection.RIGID));
    private static final Logger LOGGER = LogManager.getLogger();

    public CastleStructure(Codec<JigsawConfiguration> codec) {
        super(codec, (context) -> {
                    // Check if the spot is valid for structure gen. If false, return nothing to signal to the game to skip this spawn attempt.
                    if (!CastleStructure.isFeatureChunk(context)) {
                        return Optional.empty();
                    }
                    // Create the pieces layout of the structure and give it to
                    else {
                        return CastleStructure.createPiecesGenerator(context);
                    }
                },
                PostPlacementProcessor.NONE);
    }

    @Override
    public GenerationStep.Decoration step() {
        return GenerationStep.Decoration.SURFACE_STRUCTURES;
    }

    public static Optional<PieceGenerator<JigsawConfiguration>> createPiecesGenerator(PieceGeneratorSupplier.Context<JigsawConfiguration> context) {
        List<PieceGenerator<JigsawConfiguration>> list = new ArrayList<>();
        // Turns the chunk coordinates into actual coordinates we can use. (Gets center of that chunk)
        BlockPos blockpos = context.chunkPos().getMiddleBlockPosition(ConfigCommon.CASTLE_HEIGHT.get());

        for (int shift1=0; shift1<3; shift1++){
            for (int shift2 =0; shift2<3; shift2++){
                for (int shiftY =0; shiftY<3; shiftY++){
                    // All a structure has to do is call this method to turn it into a jigsaw based structure!
                    int finalShift1 = shift1;
                    int finalShift2 = shift2;
                    int finalShiftY = shiftY;

                    context.config().startPool =
                            () -> context.registryAccess().ownedRegistryOrThrow(Registry.TEMPLATE_POOL_REGISTRY)
                                    .get(new ResourceLocation(CastleInTheSky.MOD_ID, String.format("castle_in_the_sky/laputa%d%d%d", finalShift1, finalShiftY, finalShift2)));


                    context.config().maxDepth = 10;
                    // All a structure has to do is call this method to turn it into a jigsaw based structure!
                    Optional<PieceGenerator<JigsawConfiguration>> structurePiecesGenerator =
                            CastleFixedRotationPlacement.addPieces(
                                    context, // Used for JigsawPlacement to get all the proper behaviors done.
                                    PoolElementStructurePiece::new, // Needed in order to create a list of jigsaw pieces when making the structure's layout.
                                    blockpos.offset(shift2*48, shiftY*48, shift1*48), // Position of the structure. Y value is ignored if last parameter is set to true.
                                    false,  // Special boundary adjustments for villages. It's... hard to explain. Keep this false and make your pieces not be partially intersecting.
                                    // Either not intersecting or fully contained will make children pieces spawn just fine. It's easier that way.
                                    false, // Place at heightmap (top land). Set this to false for structure to be place at the passed in blockpos's Y value instead.
                                    // Definitely keep this false when placing structures in the nether as otherwise, heightmap placing will put the structure on the Bedrock roof.
                                    Rotation.NONE
                            );
                    structurePiecesGenerator.ifPresent(list::add);

                }
            }
        }


        return Optional.of(new PieceGeneratorList(list));
    }

    private static boolean isFeatureChunk(PieceGeneratorSupplier.Context<JigsawConfiguration> context) {
        BlockPos blockPos = context.chunkPos().getWorldPosition();

        return !blockPos.closerThan(new Vec3i(0, 0, 0), ConfigCommon.CASTLE_SPAWN_PROOF.get());
    }


    public static class FakeRandom extends Random{
        @Override
        public int nextInt(int bound) {
            return 0;
        }
    }

}
