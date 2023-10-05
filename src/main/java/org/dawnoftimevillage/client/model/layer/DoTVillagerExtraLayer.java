package org.dawnoftimevillage.client.model.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import org.dawnoftimevillage.client.model.DoTVillagerModel;
import org.dawnoftimevillage.client.renderer.DoTVillagerRenderer;
import org.dawnoftimevillage.util.DoTVUtils;
import org.dawnoftimevillage.world.entity.DoTVillager;
import org.dawnoftimevillage.world.entity.DoTVillagerCulture;
import org.dawnoftimevillage.world.entity.DoTVillagerProfession;

public class DoTVillagerExtraLayer<T extends DoTVillager, M extends DoTVillagerModel<T>> extends RenderLayer<T, M> {
    public DoTVillagerExtraLayer(DoTVillagerRenderer renderer) {
        super((RenderLayerParent<T, M>) renderer);
    }

    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T villager, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!villager.isInvisible()) {
            M model = getParentModel();
            renderCultureClothes(model, poseStack, buffer, packedLight, villager);
            renderProfessionClothes(model, poseStack, buffer, packedLight, villager);
        }
    }

    private void renderCultureClothes(M model, PoseStack poseStack, MultiBufferSource buffer, int packedLight, T villager) {
        DoTVillagerCulture culture = villager.getCulture();
        String path = "textures/entity/dot_villager/culture/dot_villager_" + culture.toString() + ".png";
        ResourceLocation resourceLocation = DoTVUtils.resource(path);
        renderColoredCutoutModel(model, resourceLocation, poseStack, buffer, packedLight, villager, 1.0F, 1.0F, 1.0F);
    }

    private void renderProfessionClothes(M model, PoseStack poseStack, MultiBufferSource buffer, int packedLight, T villager) {
        DoTVillagerProfession profession = villager.getProfession();
        if (profession != DoTVillagerProfession.UNEMPLOYED) {
            String path = "textures/entity/dot_villager/profession/dot_villager_" + profession.toString() + ".png";
            ResourceLocation resourceLocation = DoTVUtils.resource(path);
            renderColoredCutoutModel(model, resourceLocation, poseStack, buffer, packedLight, villager, 1.0F, 1.0F, 1.0F);
        }
    }

    private void renderXpLevel(M model, PoseStack poseStack, MultiBufferSource buffer, int packedLight, T villager) {
    }
}