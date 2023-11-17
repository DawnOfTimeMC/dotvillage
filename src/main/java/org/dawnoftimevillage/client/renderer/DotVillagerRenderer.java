package org.dawnoftimevillage.client.renderer;

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
import org.dawnoftimevillage.client.model.DotVillagerModel;
import org.dawnoftimevillage.client.model.layer.DotVillagerExtraLayer;
import org.dawnoftimevillage.entity.DotVillager;
import org.dawnoftimevillage.util.DotvUtils;

@OnlyIn(Dist.CLIENT)
public class DotVillagerRenderer extends HumanoidMobRenderer<DotVillager, DotVillagerModel<DotVillager>> {
    private static final ResourceLocation DOT_VILLAGER_NAKED_SKIN = DotvUtils.resource("textures/entity/villager/base_skin.png");

    public DotVillagerRenderer(EntityRendererProvider.Context context) {
        super(context, new DotVillagerModel<>(context.bakeLayer(DotVillagerModel.LAYER_LOCATION)), 0.5F);
        this.addLayer(new DotVillagerExtraLayer<>(this));
        // TODO : make an armor that fit well the villager body, especially the head
        this.addLayer(new HumanoidArmorLayer<>(this, new HumanoidArmorModel<>(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)), new HumanoidArmorModel<>(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)), context.getModelManager()));
        //this.addLayer(new CrossedArmsItemLayer<>(this, pContext.getItemInHandRenderer()));
    }

    public void render(DotVillager villager, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int packedLight) {
        getModel().setCrossedArms(villager.isCrossingArms());
        super.render(villager, entityYaw, partialTicks, matrixStack, buffer, packedLight);
    }

    protected void scale(DotVillager villager, PoseStack matrixStack, float partialTickTime) {
        float f = 0.9375F;
        if (villager.isBaby()) {
            f *= 0.5F;
            this.shadowRadius = 0.25F;
        } else {
            this.shadowRadius = 0.5F;
        }
        matrixStack.scale(f, f, f);
    }

    public ResourceLocation getTextureLocation(DotVillager villager) {return DOT_VILLAGER_NAKED_SKIN;}
}
