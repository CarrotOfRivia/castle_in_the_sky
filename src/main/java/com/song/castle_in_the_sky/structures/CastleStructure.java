package com.song.castle_in_the_sky.structures;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.song.castle_in_the_sky.config.ConfigCommon;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Hugely inspired from this tutorial: https://github.com/TelepathicGrunt/StructureTutorialMod
 */

public class CastleStructure extends Structure{

    // A custom codec that changes the size limit for our code_structure_sky_fan.json's config to not be capped at 7.
    // With this, we can have a structure with a size limit up to 30 if we want to have extremely long branches of pieces in the structure.
    public static final Codec<CastleStructure> CODEC = RecordCodecBuilder.<CastleStructure>mapCodec(instance ->
            instance.group(CastleStructure.settingsCodec(instance),
                    StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(structure -> structure.startPool),
                    ResourceLocation.CODEC.optionalFieldOf("start_jigsaw_name").forGetter(structure -> structure.startJigsawName),
                    Codec.intRange(0, 30).fieldOf("size").forGetter(structure -> structure.size),
                    HeightProvider.CODEC.fieldOf("start_height").forGetter(structure -> structure.startHeight),
                    Heightmap.Types.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter(structure -> structure.projectStartToHeightmap),
                    Codec.intRange(1, 128).fieldOf("max_distance_from_center").forGetter(structure -> structure.maxDistanceFromCenter)
            ).apply(instance, CastleStructure::new)).codec();

    private final Holder<StructureTemplatePool> startPool;
    private final Optional<ResourceLocation> startJigsawName;
    private final int size;
    private final HeightProvider startHeight;
    private final Optional<Heightmap.Types> projectStartToHeightmap;
    private final int maxDistanceFromCenter;

    public CastleStructure(Structure.StructureSettings config,
                         Holder<StructureTemplatePool> startPool,
                         Optional<ResourceLocation> startJigsawName,
                         int size,
                         HeightProvider startHeight,
                         Optional<Heightmap.Types> projectStartToHeightmap,
                         int maxDistanceFromCenter)
    {
        super(config);
        this.startPool = startPool;
        this.startJigsawName = startJigsawName;
        this.size = size;
        this.startHeight = startHeight;
        this.projectStartToHeightmap = projectStartToHeightmap;
        this.maxDistanceFromCenter = maxDistanceFromCenter;
    }

    @Override
    public Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext context) {


        // Set's our spawning blockpos's y offset to be 60 blocks up.
        // Since we are going to have heightmap/terrain height spawning set to true further down, this will make it so we spawn 60 blocks above terrain.
        // If we wanted to spawn on ocean floor, we would set heightmap/terrain height spawning to false and the grab the y value of the terrain with OCEAN_FLOOR_WG heightmap.
        int startY = ConfigCommon.CASTLE_HEIGHT.get();

        // Turns the chunk coordinates into actual coordinates we can use. (Gets corner of that chunk)
        ChunkPos chunkPos = context.chunkPos();
        BlockPos blockPos = new BlockPos(chunkPos.getMinBlockX(), startY, chunkPos.getMinBlockZ());

        if (blockPos.closerThan(new Vec3i(0, startY, 0), ConfigCommon.CASTLE_SPAWN_PROOF.get())){
            return Optional.empty();
        }

        BlockPos generatorPos = new BlockPos(0, 0, 0);
        ArrayList<Consumer<StructurePiecesBuilder>> actions = new ArrayList<>();
        int idx = 0;

        for (int shift1=0; shift1<3; shift1++) {
            for (int shift2 = 0; shift2 < 3; shift2++) {
                for (int shiftY = 0; shiftY < 3; shiftY++) {
                    Optional<Structure.GenerationStub> structurePiecesGenerator =
                            CastlePlacement.addPieces(
                                    context, // Used for JigsawPlacement to get all the proper behaviors done.
                                    this.startPool, // The starting pool to use to create the structure layout from
                                    // Can be used to only spawn from one Jigsaw block. But we don't need to worry about this.
                                    this.size, // How deep a branch of pieces can go away from center piece. (5 means branches cannot be longer than 5 pieces from center piece)
                                    blockPos.offset(shift2*48, shiftY*48, shift1*48), // Where to spawn the structure.
                                    false, // "useExpansionHack" This is for legacy villages to generate properly. You should keep this false always.
                                    Optional.empty(), // Adds the terrain height's y value to the passed in blockpos's y value. (This uses WORLD_SURFACE_WG heightmap which stops at top water too)
                                    // Here, blockpos's y value is 60 which means the structure spawn 60 blocks above terrain height.
                                    // Set this to false for structure to be place only at the passed in blockpos's Y value instead.
                                    // Definitely keep this false when placing structures in the nether as otherwise, heightmap placing will put the structure on the Bedrock roof.
                                    this.maxDistanceFromCenter,
                                    idx); // Maximum limit for how far pieces can spawn from center. You cannot set this bigger than 128 or else pieces gets cutoff.
                    actions.add(structurePiecesGenerator.get().generator().left().get());

                    idx += 1;
                }
            }
        }

        /*
         * Note, you are always free to make your own JigsawPlacement class and implementation of how the structure
         * should generate. It is tricky but extremely powerful if you are doing something that vanilla's jigsaw system cannot do.
         * Such as for example, forcing 3 pieces to always spawn every time, limiting how often a piece spawns, or remove the intersection limitation of pieces.
         */

        // Return the pieces generator that is now set up so that the game runs it when it needs to create the layout of structure pieces.
        return Optional.of(new GenerationStub(blockPos, ((structurePiecesBuilder) -> {for(Consumer<StructurePiecesBuilder> action: actions){ action.accept(structurePiecesBuilder);}
        })));
    }

    @Override
    public StructureType<?> type() {
        return StructureRegister.CASTLE_IN_THE_SKY.get();
    }
}
