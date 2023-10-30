package org.dawnoftimevillage.client.model;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import org.dawnoftimevillage.entity.DotVillager;
import org.dawnoftimevillage.util.DotvUtils;

public class DotVillagerModel<T extends DotVillager> extends HumanoidModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(DotvUtils.resource("dot_villager"), "main_layer");
    private ModelPart crossedArms;

    public DotVillagerModel(ModelPart root) {
        super(root);
        this.crossedArms = this.body.getChild("crossed_arms");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshDefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0f);
        PartDefinition root = meshDefinition.getRoot();

        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F), PartPose.ZERO);

        head.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(24, 0).addBox(-1.0F, -1.0F, -6.0F, 2.0F, 4.0F, 2.0F), PartPose.offset(0.0F, -2.0F, 0.0F));

        PartDefinition hat = root.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.51F)), PartPose.ZERO);

        hat.addOrReplaceChild("hat_rim", CubeListBuilder.create().texOffs(30, 47).addBox(-8.0F, -8.0F, -6.0F, 16.0F, 16.0F, 1.0F), PartPose.rotation((-(float)Math.PI / 2F), 0.0F, 0.0F));

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 20).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F), PartPose.ZERO);

        body.addOrReplaceChild("jacket", CubeListBuilder.create().texOffs(0, 38).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 20.0F, 6.0F, new CubeDeformation(0.5F)), PartPose.ZERO);

        PartDefinition rightArm = root.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(44, 22).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition leftArm =  root.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(44, 22).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F,  new CubeDeformation(0.0F)). mirror(false), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition rightLeg = root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 22).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F), PartPose.offset(-2.0F, 12.0F, 0.0F));

        PartDefinition leftLeg = root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 22).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F), PartPose.offset(2.0F, 12.0F, 0.0F));

        PartDefinition crossedArms = body.addOrReplaceChild("crossed_arms", CubeListBuilder.create().texOffs(40, 38).addBox(-4.0F, 2.0F, -2.0F, 8.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(44, 22).addBox(-8.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(44, 22).mirror().addBox(4.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 3.0F, -1.0F, -0.75F, 0.0F, 0.0F));

        return LayerDefinition.create(meshDefinition, 64, 64);
    }

    public void setupAnim(T villager, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(villager, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        animVillagerReadingStuff(villager);
    }

    private void animVillagerReadingStuff(T villager) {
        if (villager.isReading()) {
            if (!villager.isLeftHanded()) {
                this.rightArm.xRot = -1.65F;
                this.rightArm.yRot = -0.36F;
                this.rightArm.zRot = 1.5F;
                this.rightArm.y += 1F;
                this.rightArm.x -= 0.75F;
                this.leftArm.xRot = -1.2F;
                this.leftArm.yRot = 0.1F;
                this.leftArm.zRot = 0.1F;
            } else {
                this.leftArm.xRot = -1.65F;
                this.leftArm.yRot = 0.36F;
                this.leftArm.zRot = -1.5F;
                this.leftArm.y += 1F;
                this.leftArm.x += 0.75F;
                this.rightArm.xRot = -1.2F;
                this.rightArm.yRot = -0.1F;
                this.rightArm.zRot = -0.1F;

            }
            this.head.xRot = 0.38F;
            this.hat.xRot = this.head.xRot;
        }
    }

    public void setCrossedArms(boolean crossedArms) {
        if (crossedArms) {
            this.rightArm.visible = false;
            this.leftArm.visible = false;
            this.crossedArms.visible = true;
        } else {
            this.rightArm.visible = true;
            this.leftArm.visible = true;
            this.crossedArms.visible = false;
        }
    }
}
