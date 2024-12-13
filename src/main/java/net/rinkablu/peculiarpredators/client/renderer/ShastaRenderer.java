package net.rinkablu.peculiarpredators.client.renderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.rinkablu.peculiarpredators.client.model.ShastaModel;
import net.rinkablu.peculiarpredators.common.entity.custom.ShastaEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ShastaRenderer extends GeoEntityRenderer<ShastaEntity> {

    public ShastaRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ShastaModel());
    }

}
