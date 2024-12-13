package net.rinkablu.peculiarpredators.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.rinkablu.peculiarpredators.client.model.BabyShastaModel;
import net.rinkablu.peculiarpredators.common.entity.custom.BabyShastaEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BabyShastaRenderer extends GeoEntityRenderer<BabyShastaEntity> {

    public BabyShastaRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BabyShastaModel());
    }

    @Override
    public void render(BabyShastaEntity entity, float entityYaw, float partialTicks, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLightIn) {

        poseStack.scale(0.15F, 0.15F, 0.15F);

        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLightIn);
    }

}
