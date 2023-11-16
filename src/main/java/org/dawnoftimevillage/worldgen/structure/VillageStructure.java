package org.dawnoftimevillage.worldgen.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.*;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import org.dawnoftimevillage.building.Building;
import org.dawnoftimevillage.culture.BuildingType;
import org.dawnoftimevillage.registry.DotvStructures;
import org.dawnoftimevillage.util.DotvLogger;
import org.dawnoftimevillage.village.Village;
import org.dawnoftimevillage.village.VillageManager;

import java.util.Optional;

public class VillageStructure extends Structure {
    public final Culture culture;
    private BlockPos position;
    private boolean addedVillage = false;
    public static final Codec<VillageStructure> CODEC = RecordCodecBuilder.create((p) -> p.group(settingsCodec(p), Culture.CODEC.fieldOf("culture").forGetter((p2) -> p2.culture)).apply(p, VillageStructure::new));

    public VillageStructure(StructureSettings settings, VillageStructure.Culture culture) {
        super(settings);
        this.culture = culture;
    }

    protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        return onTopOfChunkCenter(context, Heightmap.Types.WORLD_SURFACE_WG, (builder) -> this.generatePieces(builder, context));
    }

    @Override
    public void afterPlace(WorldGenLevel pLevel, StructureManager pStructureManager, ChunkGenerator pChunkGenerator, RandomSource pRandom, BoundingBox pBoundingBox, ChunkPos pChunkPos, PiecesContainer pPieces) {
        createVillage(pLevel.getLevel(), pPieces);
        super.afterPlace(pLevel, pStructureManager, pChunkGenerator, pRandom, pBoundingBox, pChunkPos, pPieces);
    }

    private void createVillage(ServerLevel level, PiecesContainer piecesContainer) {
        if (!this.addedVillage) {
            DotvLogger.info("Adding a new village");
            Village village = VillageManager.addVillage(level.getLevel(), this.position, new org.dawnoftimevillage.culture.Culture("dummy"));

            // Adding building information to the Village class
            if (village != null) {
                var pieces = piecesContainer.pieces();
                for (StructurePiece piece : pieces) {
                    if (piece instanceof VillagePieces.VillagePiece buildingPiece) {
                        BuildingType buildingType = buildingPiece.getBuildingType();
                        Building building = Building.create(buildingType);
                        village.addBuilding(building);
                    }
                }
            }
            this.addedVillage = true;
        }
    }

    private void generatePieces(StructurePiecesBuilder builder, Structure.GenerationContext context) {
        int height = context.chunkGenerator().getFirstOccupiedHeight(context.chunkPos().getMinBlockX(), context.chunkPos().getMinBlockZ(), Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState());
        BlockPos blockpos = new BlockPos(context.chunkPos().getMinBlockX(), height - 1, context.chunkPos().getMinBlockZ());
        this.position = blockpos;
        //BlockPos blockpos = new BlockPos(context.chunkPos().getMinBlockX(), 90, context.chunkPos().getMinBlockZ());
        VillagePieces.addPieces(context.structureTemplateManager(), blockpos, builder, context.random(), this.culture);
    }

    public StructureType<?> type() {
        return DotvStructures.VILLAGE_STRUCTURE.get();
    }

    public enum Culture implements StringRepresentable {
        PLAINS("plains"),
        TAIGA("taiga");

        public static final Codec<VillageStructure.Culture> CODEC = StringRepresentable.fromEnum(VillageStructure.Culture::values);
        private final String name;

        Culture(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public String getSerializedName() {
            return this.name;
        }
    }
}
