package net.rinkablu.peculiarpredators.client.model;

import net.minecraft.resources.ResourceLocation;
import net.rinkablu.peculiarpredators.PeculiarPredators;
import net.rinkablu.peculiarpredators.common.entity.custom.YutyEntity;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class YutyModel extends GeoModel<YutyEntity> {

    @Override
    public ResourceLocation getModelResource(YutyEntity animatable) {
        if (animatable.isSaddled()){
            return new ResourceLocation(PeculiarPredators.MODID, "geo/yuty_saddled.geo.json");
        }else {
            return new ResourceLocation(PeculiarPredators.MODID, "geo/yuty.geo.json");
        }
    }

    @Override
    public ResourceLocation getTextureResource(YutyEntity animatable) {

        return new ResourceLocation(PeculiarPredators.MODID, "textures/entity/yuty.png");

    }

    @Override
    public ResourceLocation getAnimationResource(YutyEntity animatable) {
        return new ResourceLocation(PeculiarPredators.MODID, "animations/yuty.animation.json");
    }

    @Override
    public void setCustomAnimations(YutyEntity animatable, long instanceId, AnimationState<YutyEntity> animationState) {

        super.setCustomAnimations(animatable, instanceId, animationState);

        if (animationState == null) return;

        CoreGeoBone head = this.getAnimationProcessor().getBone("look_control");

        EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
        head.setRotX((entityData.headPitch() * ((float) Math.PI / 180F)));
        head.setRotY(entityData.netHeadYaw() * ((float) Math.PI / 180F));
    }

//    @Override
//    public RenderType getRenderType(YutyEntity animatable, ResourceLocation texture) {
//        return RenderType.entityCutout(texture);
//    }

}
