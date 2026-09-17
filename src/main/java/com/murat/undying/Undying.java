package com.murat.undying;

import com.murat.undying.config.UndyingConfig;
import com.murat.undying.infection.ModEffects;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(Undying.MODID)
public class Undying {

    public static final String MODID = "undying";
    public static final Logger LOG = LoggerFactory.getLogger("Undying");

    public Undying() {
        var bus = FMLJavaModLoadingContext.get().getModEventBus();

        ModEffects.REGISTRY.register(bus);

        ModLoadingContext.get().registerConfig(
                ModConfig.Type.COMMON,
                UndyingConfig.SPEC,
                "undying-common.toml"
        );

        MinecraftForge.EVENT_BUS.register(com.murat.undying.event.SenseEvents.class);
        MinecraftForge.EVENT_BUS.register(com.murat.undying.event.ZombieEvents.class);
        MinecraftForge.EVENT_BUS.register(com.murat.undying.event.InfectionEvents.class);
    }
}
