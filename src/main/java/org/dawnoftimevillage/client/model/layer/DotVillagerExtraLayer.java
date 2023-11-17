package org.dawnoftimevillage.client.model.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import org.dawnoftimevillage.client.model.DotVillagerModel;
import org.dawnoftimevillage.client.renderer.DotVillagerRenderer;
import org.dawnoftimevillage.entity.DotVillager;
import org.dawnoftimevillage.entity.DotVillagerCulture;
import org.dawnoftimevillage.entity.DotVillagerProfession;
import org.dawnoftimevillage.util.DotvUtils;


/**
 * Draws villagers culture clothes and profession clothes
 */
public class DotVillagerExtraLayer<T extends DotVillager, M extends DotVillagerModel<T>> extends RenderLayer<T, M> {
    public DotVillagerExtraLayer(DotVillagerRenderer renderer) {
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
        DotVillagerCulture culture = villager.getCulture();
        String path = "textures/entity/villager/culture_clothes/" + culture.toString() + ".png";
        ResourceLocation resourceLocation = DotvUtils.resource(path);
        renderColoredCutoutModel(model, resourceLocation, poseStack, buffer, packedLight, villager, 1.0F, 1.0F, 1.0F);
    }

    private void renderProfessionClothes(M model, PoseStack poseStack, MultiBufferSource buffer, int packedLight, T villager) {
        DotVillagerProfession profession = villager.getProfession();
        if (profession != DotVillagerProfession.UNEMPLOYED) {
            String path = "textures/entity/villager/profession/" + profession.toString() + ".png";
            ResourceLocation resourceLocation = DotvUtils.resource(path);
            renderColoredCutoutModel(model, resourceLocation, poseStack, buffer, packedLight, villager, 1.0F, 1.0F, 1.0F);
        }
    }

    private void renderXpLevel(M model, PoseStack poseStack, MultiBufferSource buffer, int packedLight, T villager) {
    }
}