package net.rinkablu.peculiarpredators.common.item;

import com.peeko32213.unusualprehistory.common.item.AnimalAttacherItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.rinkablu.peculiarpredators.PeculiarPredators;
import net.rinkablu.peculiarpredators.common.entity.PPEntities;
import net.rinkablu.peculiarpredators.util.PPTags;

public class PPItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, PeculiarPredators.MODID);

    public static final RegistryObject<Item> TROODON_SPAWN_EGG = ITEMS.register("troodon_spawn_egg",
            ()-> new ForgeSpawnEggItem(PPEntities.TROODON, 0x170d34, 0xdc614c, new Item.Properties()));

    public static final RegistryObject<Item> RAW_TROODON_DRUMSTICK =  ITEMS.register("raw_troodon_drumstick",
            ()-> new Item(new Item.Properties().food(PPFoods.RAW_TROODON_DRUMSTICK)));

    public static final RegistryObject<Item> COOKED_TROODON_DRUMSTICK =  ITEMS.register("cooked_troodon_drumstick",
            ()-> new Item(new Item.Properties().food(PPFoods.COOKED_TROODON_DRUMSTICK)));

    public static final RegistryObject<Item> TROODON_FLASK =  ITEMS.register("troodon_flask",
            ()-> new Item(new Item.Properties()));



    public static final RegistryObject<Item> YUTY_FLASK =  ITEMS.register("yuty_flask",
            ()-> new Item(new Item.Properties()));

    public static final RegistryObject<Item> YUTY_SPAWN_EGG = ITEMS.register("yuty_spawn_egg",
            ()-> new ForgeSpawnEggItem(PPEntities.YUTY, 0xe4eaea, 0x544e4e, new Item.Properties()));



    public static final RegistryObject<Item> SHASTA_FLASK =  ITEMS.register("shasta_flask",
            ()-> new Item(new Item.Properties()));

    public static final RegistryObject<Item> SHASTA_SPAWN_EGG = ITEMS.register("shasta_spawn_egg",
            ()-> new ForgeSpawnEggItem(PPEntities.SHASTA, 0x292045, 0x9b8ec0, new Item.Properties()));

    public static final RegistryObject<Item> SHASTA_EMBRYO = ITEMS.register("shasta_embryo",
            ()-> new AnimalAttacherItem(new Item.Properties().stacksTo(16), PPTags.EntityTypes.SHASTA_EMBRYO_ATTACH_TO,
                    PPEntities.BABY_SHASTA, 1000));

    public static final RegistryObject<Item> RAW_SHASTA_MEAT =  ITEMS.register("raw_shasta_meat",
            ()-> new Item(new Item.Properties().food(PPFoods.RAW_SHASTA_MEAT)));

    public static final RegistryObject<Item> COOKED_SHASTA_MEAT =  ITEMS.register("cooked_shasta_meat",
            ()-> new Item(new Item.Properties().food(PPFoods.COOKED_SHASTA_MEAT)));



    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
