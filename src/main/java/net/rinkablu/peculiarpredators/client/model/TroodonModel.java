package net.rinkablu.peculiarpredators.client.model;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.rinkablu.peculiarpredators.PeculiarPredators;
import net.rinkablu.peculiarpredators.common.entity.custom.TroodonEntity;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class TroodonModel extends GeoModel<TroodonEntity> {
    @Override
    public ResourceLocation getModelResource(TroodonEntity animatable) {
        return new ResourceLocation(PeculiarPredators.MODID, "geo/troodon.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(TroodonEntity animatable) {
        if (animatable.getVariant()==0){
            return new ResourceLocation(PeculiarPredators.MODID, "textures/entity/troodon.png");
        }else {
            return new ResourceLocation(PeculiarPredators.MODID, "textures/entity/troodon_blue.png");
        }
    }

    @Override
    public ResourceLocation getAnimationResource(TroodonEntity animatable) {
        return new ResourceLocation(PeculiarPredators.MODID, "animations/troodon.animation.json");
    }

    @Override
    public void setCustomAnimations(TroodonEntity animatable, long instanceId, AnimationState<TroodonEntity> animationState) {

        super.setCustomAnimations(animatable, instanceId, animationState);

        if (animationState == null) return;

        CoreGeoBone head = this.getAnimationProcessor().getBone("look_control");

        EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
        head.setRotX((entityData.headPitch() * ((float) Math.PI / 180F)));
        head.setRotY(entityData.netHeadYaw() * ((float) Math.PI / 180F));
    }

    @Override
    public RenderType getRenderType(TroodonEntity animatable, ResourceLocation texture) {
        return RenderType.entityCutout(texture);
    }
}
