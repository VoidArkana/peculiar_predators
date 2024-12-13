package net.rinkablu.peculiarpredators.common.block;

import com.peeko32213.unusualprehistory.common.block.BlockDinosaurLandEggs;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.rinkablu.peculiarpredators.PeculiarPredators;
import net.rinkablu.peculiarpredators.common.entity.PPEntities;
import net.rinkablu.peculiarpredators.common.item.PPItems;

import java.util.function.Supplier;

public class PPBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, PeculiarPredators.MODID);

    public static final RegistryObject<Block> TROODON_EGGS = registerBlock("troodon_eggs",
            ()-> new BlockDinosaurLandEggs(BlockBehaviour.Properties.copy(Blocks.TURTLE_EGG).strength(0.5F).sound(SoundType.METAL).randomTicks().noOcclusion(),
                    PPEntities.TROODON, 4, Block.box(6, 0, 7, 9, 4, 10), Block.box(6, 0, 6, 12, 5, 12)));

    public static final RegistryObject<Block> YUTY_EGG = registerBlock("yuty_egg",
            ()-> new BlockDinosaurLandEggs(BlockBehaviour.Properties.copy(Blocks.TURTLE_EGG).strength(0.5F).sound(SoundType.METAL).randomTicks().noOcclusion(),
                    PPEntities.YUTY, 1, Block.box(5, 0, 5, 11, 8, 11)));

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block){
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block>RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block){
        return PPItems.ITEMS.register(name, ()-> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus){
        BLOCKS.register(eventBus);
    }
}
