package net.rinkablu.peculiarpredators.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.rinkablu.peculiarpredators.client.model.YutyModel;
import net.rinkablu.peculiarpredators.common.entity.custom.YutyEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class YutyRenderer extends GeoEntityRenderer<YutyEntity> {

    public YutyRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new YutyModel());
    }

    @Override
    public void render(YutyEntity entity, float entityYaw, float partialTicks, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLightIn) {

        if (entity.isBaby()){
            poseStack.scale(0.25F, 0.25F, 0.25F);
        }else {
            poseStack.scale(1F, 1F, 1F);
        }
        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLightIn);
    }


}
