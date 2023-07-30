package com.song.castle_in_the_sky.structures;

import com.google.common.collect.Lists;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pools.EmptyPoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;

import java.util.List;
import java.util.Optional;

public class CastlePlacement {

    public static Optional<Structure.GenerationStub> addPieces(Structure.GenerationContext context, Holder<StructureTemplatePool> templatePoolHolder, int size, BlockPos spawnPos, boolean useExpansionHack, Optional<Heightmap.Types> height, int maxDistanceFromCenter, int pieceId) {
        RegistryAccess registryaccess = context.registryAccess();
        ChunkGenerator chunkgenerator = context.chunkGenerator();
        StructureTemplateManager structuretemplatemanager = context.structureTemplateManager();
        LevelHeightAccessor levelheightaccessor = context.heightAccessor();
        WorldgenRandom worldgenrandom = context.random();
        Registry<StructureTemplatePool> registry = registryaccess.registryOrThrow(Registries.TEMPLATE_POOL);
        Rotation rotation = Rotation.NONE;
        StructureTemplatePool structuretemplatepool = templatePoolHolder.value();
        NotSoRandom randomSource = new NotSoRandom(99, pieceId);
        StructurePoolElement structurepoolelement = structuretemplatepool.getRandomTemplate(randomSource);
        if (structurepoolelement == EmptyPoolElement.INSTANCE) {
            return Optional.empty();
        } else {
            BlockPos blockpos = spawnPos;
            Vec3i vec3i = blockpos.subtract(spawnPos);
            BlockPos blockpos1 = spawnPos.subtract(vec3i);
            PoolElementStructurePiece poolelementstructurepiece = new PoolElementStructurePiece(structuretemplatemanager, structurepoolelement, blockpos1, structurepoolelement.getGroundLevelDelta(), rotation, structurepoolelement.getBoundingBox(structuretemplatemanager, blockpos1, rotation));
            BoundingBox boundingbox = poolelementstructurepiece.getBoundingBox();
            int i = (boundingbox.maxX() + boundingbox.minX()) / 2;
            int j = (boundingbox.maxZ() + boundingbox.minZ()) / 2;
            int k;
            if (height.isPresent()) {
                k = spawnPos.getY() + chunkgenerator.getFirstFreeHeight(i, j, height.get(), levelheightaccessor, context.randomState());
            } else {
                k = blockpos1.getY();
            }

            int l = boundingbox.minY() + poolelementstructurepiece.getGroundLevelDelta();
            poolelementstructurepiece.move(0, k - l, 0);
            int i1 = k + vec3i.getY();
            return Optional.of(new Structure.GenerationStub(new BlockPos(i, i1, j), (p_227237_) -> {
                List<PoolElementStructurePiece> list = Lists.newArrayList();
                list.add(poolelementstructurepiece);
                if (size > 0) {
                    AABB aabb = new AABB((double)(i - maxDistanceFromCenter), (double)(i1 - maxDistanceFromCenter), (double)(j - maxDistanceFromCenter), (double)(i + maxDistanceFromCenter + 1), (double)(i1 + maxDistanceFromCenter + 1), (double)(j + maxDistanceFromCenter + 1));
                    VoxelShape voxelshape = Shapes.join(Shapes.create(aabb), Shapes.create(AABB.of(boundingbox)), BooleanOp.ONLY_FIRST);
                    JigsawPlacement.addPieces(context.randomState(), size, useExpansionHack, chunkgenerator, structuretemplatemanager, levelheightaccessor, worldgenrandom, registry, poolelementstructurepiece, list, voxelshape);
                    list.forEach(p_227237_::addPiece);
                }
            }));
        }
    }

    private static class NotSoRandom extends LegacyRandomSource {
        private final int destiny;

        public NotSoRandom(long seed, int destiny) {
            super(seed);
            this.destiny = destiny;
        }

        @Override
        public int nextInt(int maxSize) {
            assert this.destiny < maxSize;
            return this.destiny;
        }
    }
}