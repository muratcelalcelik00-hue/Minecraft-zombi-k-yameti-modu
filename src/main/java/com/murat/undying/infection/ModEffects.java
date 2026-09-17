package com.murat.undying.infection;

import com.murat.undying.Undying;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEffects {

    public static final DeferredRegister<MobEffect> REGISTRY =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, Undying.MODID);

    public static final RegistryObject<MobEffect> INFECTION =
            REGISTRY.register("infection", InfectionEffect::new);
}
