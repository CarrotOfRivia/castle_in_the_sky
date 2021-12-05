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
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraft.world.level.levelgen.feature.structures.JigsawPlacement;
import net.minecraft.world.level.levelgen.feature.structures.StructurePoolElement;
import net.minecraft.world.level.levelgen.feature.structures.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.PostPlacementProcessor;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
        // Turns the chunk coordinates into actual coordinates we can use. (Gets center of that chunk)
        BlockPos blockpos = context.chunkPos().getMiddleBlockPosition(0);

        /*
         * If you are doing Nether structures, you'll probably want to spawn your structure on top of ledges.
         * Best way to do that is to use getBaseColumn to grab a column of blocks at the structure's x/z position.
         * Then loop through it and look for land with air above it and set blockpos's Y value to it.
         * Make sure to set the final boolean in JigsawPlacement.addPieces to false so
         * that the structure spawns at blockpos's y value instead of placing the structure on the Bedrock roof!
         */
        // NoiseColumn blockReader = context.chunkGenerator().getBaseColumn(blockpos.getX(), blockpos.getZ(), context.heightAccessor());

        // We now can access out json template pool so lets set the context to use that json pool.
        // Requires AccessTransformer  (see resources/META-INF/accesstransformer.cfg)
        context.config().startPool =
                () -> context.registryAccess().ownedRegistryOrThrow(Registry.TEMPLATE_POOL_REGISTRY)
                        // The path to the starting Template Pool JSON file to read.
                        //
                        // Note, this is "structure_tutorial:run_down_house/start_pool" which means
                        // the game will automatically look into the following path for the template pool:
                        // "resources/data/structure_tutorial/worldgen/template_pool/run_down_house/start_pool.json"
                        // This is why your pool files must be in "data/<modid>/worldgen/template_pool/<the path to the pool here>"
                        // because the game automatically will check in worldgen/template_pool for the pools.
                        .get(new ResourceLocation(CastleInTheSky.MOD_ID, "castle_in_the_sky/laputa000"));

        // How many pieces outward from center can a recursive jigsaw structure spawn.
        // Our structure is only 1 piece outward and isn't recursive so any value of 1 or more doesn't change anything.
        // However, I recommend you keep this a decent value like 10 so people can use datapacks to add additional pieces to your structure easily.
        // But don't make it too large for recursive structures like villages or you'll crash server due to hundreds of pieces attempting to generate!
        // Requires AccessTransformer  (see resources/META-INF/accesstransformer.cfg)
        context.config().maxDepth = 10;

        // All a structure has to do is call this method to turn it into a jigsaw based structure!
        Optional<PieceGenerator<JigsawConfiguration>> structurePiecesGenerator =
                JigsawPlacement.addPieces(
                        context, // Used for JigsawPlacement to get all the proper behaviors done.
                        PoolElementStructurePiece::new, // Needed in order to create a list of jigsaw pieces when making the structure's layout.
                        blockpos, // Position of the structure. Y value is ignored if last parameter is set to true.
                        false,  // Special boundary adjustments for villages. It's... hard to explain. Keep this false and make your pieces not be partially intersecting.
                        // Either not intersecting or fully contained will make children pieces spawn just fine. It's easier that way.
                        true // Place at heightmap (top land). Set this to false for structure to be place at the passed in blockpos's Y value instead.
                        // Definitely keep this false when placing structures in the nether as otherwise, heightmap placing will put the structure on the Bedrock roof.
                );
        /*
         * Note, you are always free to make your own JigsawPlacement class and implementation of how the structure
         * should generate. It is tricky but extremely powerful if you are doing something that vanilla's jigsaw system cannot do.
         *
         * The only reason we are using JigsawConfiguration here is because in RunDownHouseStructure's createPiecesGenerator method,
         * we are using JigsawPlacement.addPieces which requires StructurePoolFeatureConfig. However, if you create your own
         * JigsawPlacement.addPieces, you could reduce the amount of workarounds that you need like line 150 and 130 above
         * and give yourself more opportunities and control over your structures.
         *
         * An example of a custom JigsawPlacement.addPieces in action can be found here (warning, it is using Mojmap mappings):
         * https://github.com/TelepathicGrunt/RepurposedStructures/blob/1.18/src/main/java/com/telepathicgrunt/repurposedstructures/world/structures/pieces/PieceLimitedJigsawManager.java
         */
        // Return the pieces generator that is now set up so that the game runs it when it needs to create the layout of structure pieces.
        return structurePiecesGenerator;
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
