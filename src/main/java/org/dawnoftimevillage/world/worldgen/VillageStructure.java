package org.dawnoftimevillage.world.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import org.dawnoftimevillage.registry.DoTVStructuresRegistry;

import java.util.Optional;

public class VillageStructure extends Structure {
    public final Culture culture;
    public static final Codec<VillageStructure> CODEC = RecordCodecBuilder.create((p) -> p.group(settingsCodec(p), Culture.CODEC.fieldOf("culture").forGetter((p2) -> p2.culture)).apply(p, VillageStructure::new));

    public VillageStructure(StructureSettings settings, VillageStructure.Culture culture) {
        super(settings);
        this.culture = culture;
    }

    protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        return onTopOfChunkCenter(context, Heightmap.Types.WORLD_SURFACE_WG, (builder) -> this.generatePieces(builder, context));
    }

    private void generatePieces(StructurePiecesBuilder builder, Structure.GenerationContext context) {
        int height = context.chunkGenerator().getFirstOccupiedHeight(context.chunkPos().getMinBlockX(), context.chunkPos().getMinBlockZ(), Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState());
        BlockPos blockpos = new BlockPos(context.chunkPos().getMinBlockX(), height - 1, context.chunkPos().getMinBlockZ());
        //BlockPos blockpos = new BlockPos(context.chunkPos().getMinBlockX(), 90, context.chunkPos().getMinBlockZ());
        VillagePieces.addPieces(context.structureTemplateManager(), blockpos, builder, context.random(), this.culture);
    }

    public StructureType<?> type() {
        return DoTVStructuresRegistry.VILLAGE_STRUCTURE.get();
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
