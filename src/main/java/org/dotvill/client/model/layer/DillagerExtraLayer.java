package org.dotvill.client.model.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import org.dotvill.client.model.DillagerModel;
import org.dotvill.client.renderer.DillagerRenderer;
import org.dotvill.entity.Dillager;
import org.dotvill.entity.DillagerCulture;
import org.dotvill.entity.DillagerProfession;
import org.dotvill.util.ModUtils;


/**
 * Draws villagers culture clothes and profession clothes
 */
public class DillagerExtraLayer<T extends Dillager, M extends DillagerModel<T>> extends RenderLayer<T, M> {
    public DillagerExtraLayer(DillagerRenderer renderer) {
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
        DillagerCulture culture = villager.getCulture();
        String path = "textures/entity/dillager/culture_clothes/" + culture.toString() + ".png";
        ResourceLocation resourceLocation = ModUtils.resource(path);
        renderColoredCutoutModel(model, resourceLocation, poseStack, buffer, packedLight, villager, 1.0F, 1.0F, 1.0F);
    }

    private void renderProfessionClothes(M model, PoseStack poseStack, MultiBufferSource buffer, int packedLight, T villager) {
        DillagerProfession profession = villager.getProfession();
        if (profession != DillagerProfession.UNEMPLOYED) {
            String path = "textures/entity/dillager/profession/" + profession.toString() + ".png";
            ResourceLocation resourceLocation = ModUtils.resource(path);
            renderColoredCutoutModel(model, resourceLocation, poseStack, buffer, packedLight, villager, 1.0F, 1.0F, 1.0F);
        }
    }

    private void renderXpLevel(M model, PoseStack poseStack, MultiBufferSource buffer, int packedLight, T villager) {
    }
}