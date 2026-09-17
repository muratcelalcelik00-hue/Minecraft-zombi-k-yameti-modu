package com.murat.undying.event;

import com.murat.undying.ai.PathingBudget;
import com.murat.undying.config.UndyingConfig;
import com.murat.undying.sense.SenseManager;
import com.murat.undying.sense.SenseType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Turns ordinary player actions into things zombies can perceive.
 *
 * Everything the player does that would make noise, leave blood or cast light
 * becomes a Stimulus. Nothing here targets a zombie directly — the world emits,
 * the zombies listen.
 */
public class SenseEvents {

    private static final int LIGHT_SCAN_INTERVAL = 40;

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent e) {
        if (e.phase != TickEvent.Phase.START) return;
        PathingBudget.resetTick();
    }

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent e) {
        if (e.phase != TickEvent.Phase.END) return;
        if (e.level.isClientSide) return;
        SenseManager.tick(e.level);
    }

    /**
     * Stimuli are keyed by dimension in a static map, so they have to be dropped
     * when the world goes away. Without this, leaving a single player world and
     * loading another keeps the first one's stimuli alive for the whole session.
     */
    @SubscribeEvent
    public static void onLevelUnload(LevelEvent.Unload e) {
        if (!(e.getLevel() instanceof ServerLevel level)) return;
        SenseManager.clear(level);
    }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent e) {
        SenseManager.clearAll();
    }

    /** Mining is loud. This is why digging into a wall at night is a bad idea. */
    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent e) {
        if (!UndyingConfig.CFG.soundEnabled.get()) return;
        if (!(e.getLevel() instanceof ServerLevel level)) return;

        BlockPos p = e.getPos();
        float hardness = e.getState().getDestroySpeed(level, p);
        double strength = UndyingConfig.CFG.soundStrengthBlockBreak.get()
                * (1.0 + Math.min(hardness, 5.0) * 0.2);

        SenseManager.add(level, Vec3.atCenterOf(p), SenseType.SOUND,
                strength, UndyingConfig.CFG.stimulusLifetimeSound.get());
    }

    /** Explosions carry a long way. TNT is a dinner bell. */
    @SubscribeEvent
    public static void onExplosion(ExplosionEvent.Detonate e) {
        if (!UndyingConfig.CFG.soundEnabled.get()) return;

        Vec3 pos = e.getExplosion().getPosition();
        SenseManager.add(e.getLevel(), pos, SenseType.SOUND,
                UndyingConfig.CFG.soundStrengthExplosion.get(),
                UndyingConfig.CFG.stimulusLifetimeSound.get());
    }

    /** Blood. You wound yourself, you leave a trail they can follow. */
    @SubscribeEvent
    public static void onHurt(LivingHurtEvent e) {
        if (!UndyingConfig.CFG.scentEnabled.get()) return;

        LivingEntity victim = e.getEntity();
        if (victim.level().isClientSide) return;
        if (!(victim instanceof Player)) return;

        double strength = UndyingConfig.CFG.scentStrengthWound.get()
                * (1.0 + Math.min(e.getAmount(), 10.0) * 0.1);

        SenseManager.add(victim.level(), victim.position(), SenseType.SCENT,
                strength, UndyingConfig.CFG.stimulusLifetimeScent.get());
    }

    /**
     * Sprinting footsteps and torchlight. Scanned on a slow interval because
     * this runs for every player, every tick otherwise.
     */
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent e) {
        if (e.phase != TickEvent.Phase.END) return;

        Player player = e.player;
        if (player.level().isClientSide) return;
        if (player.isSpectator() || player.isCreative()) return;

        long time = player.level().getGameTime();

        if (UndyingConfig.CFG.soundEnabled.get()
                && player.isSprinting()
                && time % 10 == 0) {
            SenseManager.add(player.level(), player.position(), SenseType.SOUND,
                    UndyingConfig.CFG.soundStrengthSprint.get(),
                    UndyingConfig.CFG.stimulusLifetimeSound.get() / 2);
        }

        if (UndyingConfig.CFG.lightEnabled.get()
                && (time + player.getId()) % LIGHT_SCAN_INTERVAL == 0) {
            BlockPos pos = player.blockPosition();
            int light = player.level().getBrightness(LightLayer.BLOCK, pos);
            if (light > 4) {
                double strength = (light - 4) * UndyingConfig.CFG.lightStrengthPerLevel.get();
                SenseManager.add(player.level(), Vec3.atCenterOf(pos), SenseType.LIGHT,
                        strength, UndyingConfig.CFG.stimulusLifetimeLight.get());
            }
        }
    }
}
