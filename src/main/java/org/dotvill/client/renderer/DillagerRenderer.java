package org.dotvill.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.dotvill.client.model.DillagerModel;
import org.dotvill.client.model.layer.DillagerExtraLayer;
import org.dotvill.entity.Dillager;
import org.dotvill.util.ModUtils;

@OnlyIn(Dist.CLIENT)
public class DillagerRenderer extends HumanoidMobRenderer<Dillager, DillagerModel<Dillager>> {
    private static final ResourceLocation DOT_VILLAGER_NAKED_SKIN = ModUtils.resource("textures/entity/dillager/base_skin.png");

    public DillagerRenderer(EntityRendererProvider.Context context) {
        super(context, new DillagerModel<>(context.bakeLayer(DillagerModel.LAYER_LOCATION)), 0.5F);
        this.addLayer(new DillagerExtraLayer<>(this));
        // TODO : make an armor that fit well the villager body, especially the head
        this.addLayer(new HumanoidArmorLayer<>(this, new HumanoidArmorModel<>(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)), new HumanoidArmorModel<>(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)), context.getModelManager()));
        //this.addLayer(new CrossedArmsItemLayer<>(this, pContext.getItemInHandRenderer()));
    }

    public void render(Dillager villager, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int packedLight) {
        getModel().setCrossedArms(villager.isCrossingArms());
        super.render(villager, entityYaw, partialTicks, matrixStack, buffer, packedLight);
    }

    protected void scale(Dillager villager, PoseStack matrixStack, float partialTickTime) {
        float f = 0.9375F;
        if (villager.isBaby()) {
            f *= 0.5F;
            this.shadowRadius = 0.25F;
        } else {
            this.shadowRadius = 0.5F;
        }
        matrixStack.scale(f, f, f);
    }

    public ResourceLocation getTextureLocation(Dillager villager) {return DOT_VILLAGER_NAKED_SKIN;}
}
