package net.rinkablu.peculiarpredators.client.model;

import net.minecraft.resources.ResourceLocation;
import net.rinkablu.peculiarpredators.PeculiarPredators;
import net.rinkablu.peculiarpredators.common.entity.custom.ShastaEntity;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class ShastaModel extends GeoModel<ShastaEntity> {

    @Override
    public ResourceLocation getModelResource(ShastaEntity animatable) {

        return new ResourceLocation(PeculiarPredators.MODID, "geo/shasta.geo.json");

    }

    @Override
    public ResourceLocation getTextureResource(ShastaEntity animatable) {

        return new ResourceLocation(PeculiarPredators.MODID, "textures/entity/shasta.png");

    }

    @Override
    public ResourceLocation getAnimationResource(ShastaEntity animatable) {
        return new ResourceLocation(PeculiarPredators.MODID, "animations/shasta.animation.json");
    }

    @Override
    public void setCustomAnimations(ShastaEntity animatable, long instanceId, AnimationState<ShastaEntity> animationState) {

        super.setCustomAnimations(animatable, instanceId, animationState);

        if (animationState == null) return;

        CoreGeoBone neck = this.getAnimationProcessor().getBone("neck_look");
        CoreGeoBone head = this.getAnimationProcessor().getBone("head_look");

        EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
        head.setRotX((entityData.headPitch() * ((float) Math.PI / 180F))/2);
        head.setRotY(entityData.netHeadYaw() * ((float) Math.PI / 180F)/2);
        neck.setRotX((entityData.headPitch() * ((float) Math.PI / 180F))/2);
        neck.setRotY(entityData.netHeadYaw() * ((float) Math.PI / 180F)/2);
    }
}
