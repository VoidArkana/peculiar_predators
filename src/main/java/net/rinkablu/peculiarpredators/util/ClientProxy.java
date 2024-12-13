package net.rinkablu.peculiarpredators.util;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod;
import net.rinkablu.peculiarpredators.PeculiarPredators;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = PeculiarPredators.MODID, value = Dist.CLIENT)
public class ClientProxy extends CommonProxy{

    public void init() {}

    public void clientInit() {}

    public Player getClientSidePlayer() {
        return Minecraft.getInstance().player;
    }
}
