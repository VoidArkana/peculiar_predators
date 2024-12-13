package net.rinkablu.peculiarpredators;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.rinkablu.peculiarpredators.common.block.PPBlocks;
import net.rinkablu.peculiarpredators.common.item.PPItems;

public class PPCreativeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, PeculiarPredators.MODID);

    public static final RegistryObject<CreativeModeTab> PECULIARPREDATORS_TAB =
            CREATIVE_MODE_TABS.register("peculiarpredators_tab", ()-> CreativeModeTab.builder().icon(() -> new ItemStack(PPItems.TROODON_SPAWN_EGG.get()))
                    .title(Component.translatable("creativetab.peculiarpredators_tab"))
                    .displayItems((itemDisplayParameters, output) -> {

                        //flasks
                        output.accept(PPItems.SHASTA_FLASK.get());
                        output.accept(PPItems.TROODON_FLASK.get());
                        output.accept(PPItems.YUTY_FLASK.get());

                        //mob drops and tools
                        output.accept(PPItems.RAW_SHASTA_MEAT.get());
                        output.accept(PPItems.COOKED_SHASTA_MEAT.get());
                        output.accept(PPItems.RAW_TROODON_DRUMSTICK.get());
                        output.accept(PPItems.COOKED_TROODON_DRUMSTICK.get());

                        //eggs
                        output.accept(PPItems.SHASTA_EMBRYO.get());
                        output.accept(PPBlocks.TROODON_EGGS.get());
                        output.accept(PPBlocks.YUTY_EGG.get());

                        //spawn eggs
                        output.accept(PPItems.SHASTA_SPAWN_EGG.get());
                        output.accept(PPItems.TROODON_SPAWN_EGG.get());
                        output.accept(PPItems.YUTY_SPAWN_EGG.get());
                    })
                    .build());

    public static void register(IEventBus eventBus){
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
