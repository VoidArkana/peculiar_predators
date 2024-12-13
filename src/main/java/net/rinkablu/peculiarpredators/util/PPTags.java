package net.rinkablu.peculiarpredators.util;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.rinkablu.peculiarpredators.PeculiarPredators;

public class PPTags {

    public static class EntityTypes {
        public static final TagKey<EntityType<?>> SHASTA_EMBRYO_ATTACH_TO = tag("shasta_embryo_attach_to");

        private static TagKey<EntityType<?>> tag(String name){
            return TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(PeculiarPredators.MODID, name));
        }
    }
}
