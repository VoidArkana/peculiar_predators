package net.rinkablu.peculiarpredators.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.kinds.IdF;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.rinkablu.peculiarpredators.client.model.TroodonModel;
import net.rinkablu.peculiarpredators.common.entity.custom.TroodonEntity;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class TroodonRenderer extends GeoEntityRenderer<TroodonEntity> {

    public TroodonRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new TroodonModel());
    }

    @Override
    public void render(TroodonEntity entity, float entityYaw, float partialTicks, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLightIn) {

        if (entity.isBaby()){
            poseStack.scale(0.5F, 0.5F, 0.5F);
        }else {
            poseStack.scale(1F, 1F, 1F);
        }
        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLightIn);
    }


}
