package net.rinkablu.peculiarpredators.util;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.common.Mod;
import net.rinkablu.peculiarpredators.PeculiarPredators;

@Mod.EventBusSubscriber(modid = PeculiarPredators.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonProxy {
    public void init() {
    }

    public void clientInit() {
    }

    public Player getClientSidePlayer() {
        return null;
    }
}
