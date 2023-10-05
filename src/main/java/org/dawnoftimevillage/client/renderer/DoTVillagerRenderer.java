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
import org.dawnoftimevillage.client.model.DoTVillagerModel;
import org.dawnoftimevillage.client.model.layer.DoTVillagerExtraLayer;
import org.dawnoftimevillage.util.DoTVUtils;
import org.dawnoftimevillage.world.entity.DoTVillager;

@OnlyIn(Dist.CLIENT)
public class DoTVillagerRenderer extends HumanoidMobRenderer<DoTVillager, DoTVillagerModel<DoTVillager>> {
    private static final ResourceLocation DOT_VILLAGER_NAKED_SKIN = DoTVUtils.resource("textures/entity/dot_villager/dot_villager_base.png");

    public DoTVillagerRenderer(EntityRendererProvider.Context context) {
        super(context, new DoTVillagerModel<>(context.bakeLayer(DoTVillagerModel.LAYER_LOCATION)), 0.5F);
        this.addLayer(new DoTVillagerExtraLayer<>(this));
        // TODO : make an armor that fit well the villager body, especially the head
        this.addLayer(new HumanoidArmorLayer<>(this, new HumanoidArmorModel<>(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)), new HumanoidArmorModel<>(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)), context.getModelManager()));
        //this.addLayer(new CrossedArmsItemLayer<>(this, pContext.getItemInHandRenderer()));
    }

    public void render(DoTVillager villager, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int packedLight) {
        getModel().setCrossedArms(villager.isCrossingArms());
        super.render(villager, entityYaw, partialTicks, matrixStack, buffer, packedLight);
    }

    protected void scale(DoTVillager villager, PoseStack matrixStack, float partialTickTime) {
        float f = 0.9375F;
        if (villager.isBaby()) {
            f *= 0.5F;
            this.shadowRadius = 0.25F;
        } else {
            this.shadowRadius = 0.5F;
        }
        matrixStack.scale(f, f, f);
    }

    public ResourceLocation getTextureLocation(DoTVillager villager) {return DOT_VILLAGER_NAKED_SKIN;}
}
