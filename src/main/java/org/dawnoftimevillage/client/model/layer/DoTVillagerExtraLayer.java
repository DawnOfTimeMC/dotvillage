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
            DoTVillagerCulture culture = villager.getCulture();
            DoTVillagerProfession profession = villager.getProfession();

            /** Culture **/
            String path = "textures/entity/dot_villager/culture/dot_villager_" + culture.toString() + ".png";
            ResourceLocation resourceLocation = DoTVUtils.resource(path);
            renderColoredCutoutModel(model, resourceLocation, poseStack, buffer, packedLight, villager, 1.0F, 1.0F, 1.0F);

            /** Profession **/
            if (profession != DoTVillagerProfession.UNEMPLOYED) {
                path = "textures/entity/dot_villager/profession/dot_villager_" + profession.toString() + ".png";
                resourceLocation = DoTVUtils.resource(path);
                renderColoredCutoutModel(model, resourceLocation, poseStack, buffer, packedLight, villager, 1.0F, 1.0F, 1.0F);
            }

            /** Level **/
            /*
            if(profession != AovVillagerProfession.NITWIT) {
                path = "textures/entity/aov_villager/profession_level/aov_villager_" + villagerLevel.toString() + ".png";
                ressourceLocation = ModUtils.resource(path);
                renderColoredCutoutModel(model, ressourceLocation, pPoseStack, pBuffer, pPackedLight, pLivingEntity, 1.0F, 1.0F, 1.0F);
            }
            */
        }
    }
}
