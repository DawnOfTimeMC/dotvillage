package org.dawnoftimevillage.util;

import net.minecraft.resources.ResourceLocation;
import org.dawnoftimevillage.DawnOfTimeVillage;

public class DoTVUtils {
    public static ResourceLocation resource(String name) {
        return new ResourceLocation(DawnOfTimeVillage.MOD_ID, name);
    }

    /*
    public static Vec3i getStructureDimensions(ResourceLocation path, ResourceManager resourceManager) {
        FileToIdConverter converter = new FileToIdConverter("structures", ".nbt");
        ResourceLocation resourceLocation = converter.idToFile(path);
        try (InputStream inputStream = resourceManager.open(resourceLocation)) {
            CompoundTag tag = NbtIo.readCompressed(inputStream);
            ListTag sizeTag = tag.getList("size", 3);
            return new Vec3i(sizeTag.getInt(0), sizeTag.getInt(1), sizeTag.getInt(2));
        } catch (FileNotFoundException fileNotFoundException) {
            LogUtils.getLogger().error("Structure not found {}", resourceLocation, fileNotFoundException);
            return null;
        } catch (Throwable throwable) {
            LogUtils.getLogger().error("Error loading structure {}", resourceLocation, throwable);
            return null;
        }
    } */
}
