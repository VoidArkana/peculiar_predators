package net.rinkablu.peculiarpredators.common.entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.rinkablu.peculiarpredators.PeculiarPredators;
import net.rinkablu.peculiarpredators.common.entity.custom.BabyShastaEntity;
import net.rinkablu.peculiarpredators.common.entity.custom.ShastaEntity;
import net.rinkablu.peculiarpredators.common.entity.custom.TroodonEntity;
import net.rinkablu.peculiarpredators.common.entity.custom.YutyEntity;

public class PPEntities {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, PeculiarPredators.MODID);

    public static final RegistryObject<EntityType<TroodonEntity>> TROODON =
            ENTITY_TYPES.register("troodon",
                    () -> EntityType.Builder.of(TroodonEntity::new, MobCategory.CREATURE)
                            .sized(0.7f, 0.9f)
                            .build(new ResourceLocation(PeculiarPredators.MODID, "troodon").toString()));

    public static final RegistryObject<EntityType<YutyEntity>> YUTY =
            ENTITY_TYPES.register("yuty",
                    () -> EntityType.Builder.of(YutyEntity::new, MobCategory.CREATURE)
                            .sized(2.5f, 3.5f)
                            .build(new ResourceLocation(PeculiarPredators.MODID, "yuty").toString()));

    public static final RegistryObject<EntityType<ShastaEntity>> SHASTA =
            ENTITY_TYPES.register("shasta",
                    () -> EntityType.Builder.of(ShastaEntity::new, MobCategory.CREATURE)
                            .sized(5f, 5f)
                            .build(new ResourceLocation(PeculiarPredators.MODID, "shasta").toString()));

    public static final RegistryObject<EntityType<BabyShastaEntity>> BABY_SHASTA =
            ENTITY_TYPES.register("baby_shasta",
                    () -> EntityType.Builder.of(BabyShastaEntity::new, MobCategory.CREATURE)
                            .sized(2f, 0.5f)
                            .build(new ResourceLocation(PeculiarPredators.MODID, "baby_shasta").toString()));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
