package com.song.castle_in_the_sky.features;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.song.castle_in_the_sky.CastleInTheSky;
import com.song.castle_in_the_sky.config.ConfigCommon;
import net.minecraft.core.*;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.data.worldgen.ProcessorLists;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.PostPlacementProcessor;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pools.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Predicate;

/**
 * Hugely inspired from this tutorial: https://github.com/TelepathicGrunt/StructureTutorialMod
 */

public class CastleStructure extends StructureFeature<JigsawConfiguration> {

    public static final Holder<StructureTemplatePool> START = Pools.register(new StructureTemplatePool(new ResourceLocation("village/desert/town_centers"), new ResourceLocation("empty"),
            ImmutableList.of(Pair.of(StructurePoolElement.legacy("village/desert/town_centers/desert_meeting_point_1"), 98)), StructureTemplatePool.Projection.RIGID));


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
    public GenerationStep.@NotNull Decoration step() {
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
                    String xyz = String.format("%d%d%d", shift1, shiftY, shift2);
//                    Holder.Reference<StructureTemplatePool> startPool = (Holder.Reference<StructureTemplatePool>) context.config().startPool();
//                    startPool.key().location = new ResourceLocation(CastleInTheSky.MOD_ID, CastleInTheSky.MOD_ID+"/laputa"+xyz);
//                    startPool.value().name = new ResourceLocation(CastleInTheSky.MOD_ID, CastleInTheSky.MOD_ID+"/laputa"+xyz);
//                    SinglePoolElement singlePoolElement = (SinglePoolElement) startPool.value().templates.get(0);
//                    singlePoolElement.template = Either.left(new ResourceLocation(CastleInTheSky.MOD_ID, "laputa"+xyz));

                    context.config().startPool = Holder.direct(context.registryAccess().ownedRegistryOrThrow(Registry.TEMPLATE_POOL_REGISTRY).get(new ResourceLocation(CastleInTheSky.MOD_ID, "castle_in_the_sky/laputa"+xyz)));

//                    context.config().startPool =
//                            () -> context.registryAccess().ownedRegistryOrThrow(Registry.TEMPLATE_POOL_REGISTRY)
//                                    .get(new ResourceLocation(CastleInTheSky.MOD_ID, String.format("castle_in_the_sky/laputa%d%d%d", finalShift1, finalShiftY, finalShift2)));

                    // All a structure has to do is call this method to turn it into a jigsaw based structure!
                    Optional<PieceGenerator<JigsawConfiguration>> structurePiecesGenerator =
                            addPieces(
                                    context, // Used for JigsawPlacement to get all the proper behaviors done.
                                    PoolElementStructurePiece::new, // Needed in order to create a list of jigsaw pieces when making the structure's layout.
                                    blockpos.offset(shift2*48, shiftY*48, shift1*48), // Position of the structure. Y value is ignored if last parameter is set to true.
                                    false,  // Special boundary adjustments for villages. It's... hard to explain. Keep this false and make your pieces not be partially intersecting.
                                    // Either not intersecting or fully contained will make children pieces spawn just fine. It's easier that way.
                                    false // Place at heightmap (top land). Set this to false for structure to be place at the passed in blockpos's Y value instead.
                                    // Definitely keep this false when placing structures in the nether as otherwise, heightmap placing will put the structure on the Bedrock roof.
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


    public static Optional<PieceGenerator<JigsawConfiguration>> addPieces(PieceGeneratorSupplier.Context<JigsawConfiguration> p_210285_, JigsawPlacement.PieceFactory p_210286_, BlockPos p_210287_, boolean p_210288_, boolean p_210289_) {
        WorldgenRandom worldgenrandom = new WorldgenRandom(new LegacyRandomSource(0L));
        worldgenrandom.setLargeFeatureSeed(p_210285_.seed(), p_210285_.chunkPos().x, p_210285_.chunkPos().z);
        RegistryAccess registryaccess = p_210285_.registryAccess();
        JigsawConfiguration jigsawconfiguration = p_210285_.config();
        ChunkGenerator chunkgenerator = p_210285_.chunkGenerator();
        StructureManager structuremanager = p_210285_.structureManager();
        LevelHeightAccessor levelheightaccessor = p_210285_.heightAccessor();
        Predicate<Holder<Biome>> predicate = p_210285_.validBiome();
        StructureFeature.bootstrap();
        Registry<StructureTemplatePool> registry = registryaccess.registryOrThrow(Registry.TEMPLATE_POOL_REGISTRY);
        Rotation rotation = Rotation.NONE;
        StructureTemplatePool structuretemplatepool = jigsawconfiguration.startPool().value();
        StructurePoolElement structurepoolelement = structuretemplatepool.getRandomTemplate(worldgenrandom);
        if (structurepoolelement == EmptyPoolElement.INSTANCE) {
            return Optional.empty();
        } else {
            PoolElementStructurePiece poolelementstructurepiece = p_210286_.create(structuremanager, structurepoolelement, p_210287_, structurepoolelement.getGroundLevelDelta(), rotation, structurepoolelement.getBoundingBox(structuremanager, p_210287_, rotation));
            BoundingBox boundingbox = poolelementstructurepiece.getBoundingBox();
            int i = (boundingbox.maxX() + boundingbox.minX()) / 2;
            int j = (boundingbox.maxZ() + boundingbox.minZ()) / 2;
            int k;
            if (p_210289_) {
                k = p_210287_.getY() + chunkgenerator.getFirstFreeHeight(i, j, Heightmap.Types.WORLD_SURFACE_WG, levelheightaccessor);
            } else {
                k = p_210287_.getY();
            }

            if (!predicate.test(chunkgenerator.getNoiseBiome(QuartPos.fromBlock(i), QuartPos.fromBlock(k), QuartPos.fromBlock(j)))) {
                return Optional.empty();
            } else {
                int l = boundingbox.minY() + poolelementstructurepiece.getGroundLevelDelta();
                poolelementstructurepiece.move(0, k - l, 0);
                return Optional.of((p_210282_, p_210283_) -> {
                    List<PoolElementStructurePiece> list = Lists.newArrayList();
                    list.add(poolelementstructurepiece);
                    if (jigsawconfiguration.maxDepth() > 0) {
                        int i1 = 80;
                        AABB aabb = new AABB((double)(i - 80), (double)(k - 80), (double)(j - 80), (double)(i + 80 + 1), (double)(k + 80 + 1), (double)(j + 80 + 1));
                        JigsawPlacement.Placer jigsawplacement$placer = new JigsawPlacement.Placer(registry, jigsawconfiguration.maxDepth(), p_210286_, chunkgenerator, structuremanager, list, worldgenrandom);
                        jigsawplacement$placer.placing.addLast(new JigsawPlacement.PieceState(poolelementstructurepiece, new MutableObject<>(Shapes.join(Shapes.create(aabb), Shapes.create(AABB.of(boundingbox)), BooleanOp.ONLY_FIRST)), 0));

                        while(!jigsawplacement$placer.placing.isEmpty()) {
                            JigsawPlacement.PieceState jigsawplacement$piecestate = jigsawplacement$placer.placing.removeFirst();
                            jigsawplacement$placer.tryPlacingChildren(jigsawplacement$piecestate.piece, jigsawplacement$piecestate.free, jigsawplacement$piecestate.depth, p_210288_, levelheightaccessor);
                        }

                        list.forEach(p_210282_::addPiece);
                    }
                });
            }
        }
    }

}
