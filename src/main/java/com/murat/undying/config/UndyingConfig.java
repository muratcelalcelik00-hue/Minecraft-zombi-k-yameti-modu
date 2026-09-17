package com.murat.undying.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class UndyingConfig {

    public static final ForgeConfigSpec SPEC;
    public static final UndyingConfig CFG;

    static {
        Pair<UndyingConfig, ForgeConfigSpec> pair =
                new ForgeConfigSpec.Builder().configure(UndyingConfig::new);
        CFG = pair.getLeft();
        SPEC = pair.getRight();
    }

    // ---------- SENSES ----------
    public final ForgeConfigSpec.BooleanValue soundEnabled;
    public final ForgeConfigSpec.BooleanValue scentEnabled;
    public final ForgeConfigSpec.BooleanValue lightEnabled;

    public final ForgeConfigSpec.IntValue senseRange;
    public final ForgeConfigSpec.IntValue senseCheckInterval;
    public final ForgeConfigSpec.IntValue maxStimuliPerLevel;

    public final ForgeConfigSpec.DoubleValue soundStrengthBlockBreak;
    public final ForgeConfigSpec.DoubleValue soundStrengthExplosion;
    public final ForgeConfigSpec.DoubleValue soundStrengthSprint;
    public final ForgeConfigSpec.DoubleValue scentStrengthWound;
    public final ForgeConfigSpec.DoubleValue lightStrengthPerLevel;

    public final ForgeConfigSpec.IntValue stimulusLifetimeSound;
    public final ForgeConfigSpec.IntValue stimulusLifetimeScent;
    public final ForgeConfigSpec.IntValue stimulusLifetimeLight;

    public final ForgeConfigSpec.IntValue investigateGiveUpTicks;

    // ---------- ATTRIBUTES ----------
    public final ForgeConfigSpec.DoubleValue followRangeMultiplier;
    public final ForgeConfigSpec.DoubleValue speedMultiplier;
    public final ForgeConfigSpec.DoubleValue attackMultiplier;
    public final ForgeConfigSpec.DoubleValue knockbackResistanceBonus;

    // ---------- SUN ----------
    public final ForgeConfigSpec.BooleanValue immuneToSunlight;

    // ---------- BLOCK BREAKING ----------
    public final ForgeConfigSpec.BooleanValue breakingEnabled;
    public final ForgeConfigSpec.DoubleValue maxBlockHardness;
    public final ForgeConfigSpec.IntValue breakTicksPerHardness;
    public final ForgeConfigSpec.BooleanValue dropBrokenBlocks;
    public final ForgeConfigSpec.BooleanValue respectMobGriefing;

    // ---------- INFECTION ----------
    public final ForgeConfigSpec.BooleanValue infectionEnabled;
    public final ForgeConfigSpec.DoubleValue infectionChance;
    public final ForgeConfigSpec.IntValue infectionDurationTicks;
    public final ForgeConfigSpec.DoubleValue infectionMaxHealthLoss;
    public final ForgeConfigSpec.BooleanValue reanimateOnDeath;
    public final ForgeConfigSpec.BooleanValue infectVillagers;

    // ---------- PERFORMANCE ----------
    public final ForgeConfigSpec.IntValue pathingBudgetPerTick;

    UndyingConfig(ForgeConfigSpec.Builder b) {

        b.comment("Zombies find you by sound, blood and light instead of magic tracking.").push("senses");
        soundEnabled = b.define("soundEnabled", true);
        scentEnabled = b.define("scentEnabled", true);
        lightEnabled = b.define("lightEnabled", true);
        senseRange = b.comment("How far a zombie can perceive a stimulus. Main CPU cost.")
                .defineInRange("senseRange", 40, 8, 128);
        senseCheckInterval = b.comment("Ticks between sense queries per zombie. Higher = cheaper.")
                .defineInRange("senseCheckInterval", 20, 5, 100);
        maxStimuliPerLevel = b.comment("Hard cap on tracked stimuli. Oldest are dropped first.")
                .defineInRange("maxStimuliPerLevel", 256, 16, 4096);
        soundStrengthBlockBreak = b.defineInRange("soundStrengthBlockBreak", 1.0, 0.0, 10.0);
        soundStrengthExplosion = b.defineInRange("soundStrengthExplosion", 4.0, 0.0, 10.0);
        soundStrengthSprint = b.defineInRange("soundStrengthSprint", 0.35, 0.0, 10.0);
        scentStrengthWound = b.comment("Blood trail. The single most important realism lever.")
                .defineInRange("scentStrengthWound", 2.5, 0.0, 10.0);
        lightStrengthPerLevel = b.comment("Per block-light level above the threshold. Torches give you away.")
                .defineInRange("lightStrengthPerLevel", 0.12, 0.0, 2.0);
        stimulusLifetimeSound = b.defineInRange("stimulusLifetimeSound", 200, 20, 6000);
        stimulusLifetimeScent = b.defineInRange("stimulusLifetimeScent", 600, 20, 6000);
        stimulusLifetimeLight = b.defineInRange("stimulusLifetimeLight", 120, 20, 6000);
        investigateGiveUpTicks = b.comment("How long a zombie keeps walking toward a stimulus before losing interest.")
                .defineInRange("investigateGiveUpTicks", 300, 40, 2400);
        b.pop();

        b.comment("Zombies are not tanky or fast. They are relentless.").push("attributes");
        followRangeMultiplier = b.defineInRange("followRangeMultiplier", 1.35, 0.5, 4.0);
        speedMultiplier = b.comment("Below 1.0 = shambling. You can outrun them, you cannot lose them.")
                .defineInRange("speedMultiplier", 0.92, 0.3, 2.0);
        attackMultiplier = b.defineInRange("attackMultiplier", 1.25, 0.1, 5.0);
        knockbackResistanceBonus = b.comment("They do not fly backwards when hit.")
                .defineInRange("knockbackResistanceBonus", 0.3, 0.0, 1.0);
        b.pop();

        b.push("sunlight");
        immuneToSunlight = b.comment("Daytime must not be a safe zone.")
                .define("immuneToSunlight", true);
        b.pop();

        b.comment("What your shelter is made of finally matters.").push("breaking");
        breakingEnabled = b.define("breakingEnabled", true);
        maxBlockHardness = b.comment("2.5 = wood, glass, dirt yes. Stone no.")
                .defineInRange("maxBlockHardness", 2.5, 0.0, 50.0);
        breakTicksPerHardness = b.comment("Ticks of pounding per point of hardness.")
                .defineInRange("breakTicksPerHardness", 60, 5, 600);
        dropBrokenBlocks = b.define("dropBrokenBlocks", false);
        respectMobGriefing = b.define("respectMobGriefing", true);
        b.pop();

        b.comment("The bite is the point of the whole mod.").push("infection");
        infectionEnabled = b.define("infectionEnabled", true);
        infectionChance = b.defineInRange("infectionChance", 0.35, 0.0, 1.0);
        infectionDurationTicks = b.comment("6000 ticks = 5 minutes. Find a cure or die.")
                .defineInRange("infectionDurationTicks", 6000, 200, 72000);
        infectionMaxHealthLoss = b.comment("Max hearts lost at full infection progress.")
                .defineInRange("infectionMaxHealthLoss", 12.0, 0.0, 18.0);
        reanimateOnDeath = b.comment("Die infected and something wearing your name gets up where you fell.")
                .define("reanimateOnDeath", true);
        infectVillagers = b.comment("The world collapses without you.")
                .define("infectVillagers", true);
        b.pop();

        b.comment("Tune these down on weak hardware before touching anything else.").push("performance");
        pathingBudgetPerTick = b.comment("Max zombies allowed to compute a path in one tick. The single biggest FPS lever.")
                .defineInRange("pathingBudgetPerTick", 10, 1, 64);
        b.pop();
    }
}
