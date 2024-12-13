package net.rinkablu.peculiarpredators.common.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

public class PPFoods {

    public static final FoodProperties RAW_TROODON_DRUMSTICK = (new FoodProperties.Builder()).nutrition(2)
            .saturationMod(0.3F)
            .effect(new MobEffectInstance(MobEffects.HUNGER, 600, 0), 0.3F)
            .meat().build();

    public static final FoodProperties COOKED_TROODON_DRUMSTICK = (new FoodProperties.Builder()).nutrition(6)
            .saturationMod(0.6F).meat().build();

    public static final FoodProperties RAW_SHASTA_MEAT = (new FoodProperties.Builder()).nutrition(4)
            .saturationMod(0.4F)
            .meat().build();

    public static final FoodProperties COOKED_SHASTA_MEAT = (new FoodProperties.Builder()).nutrition(10)
            .saturationMod(1F).meat().build();

}
