package com.murat.undying.sense;

import com.murat.undying.config.UndyingConfig;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

/**
 * Holds every live stimulus, per dimension.
 *
 * Deliberately not an entity and not SavedData: stimuli are short-lived and
 * worthless across a restart, so keeping them in memory avoids both the
 * entity tick cost and the NBT write cost.
 */
public final class SenseManager {

    /** How often the whole deque is swept for expired entries, in ticks. */
    private static final int SWEEP_INTERVAL = 100;

    private static final Map<ResourceKey<Level>, Deque<Stimulus>> LEVELS = new HashMap<>();

    private SenseManager() {}

    public static void add(Level level, Vec3 pos, SenseType type, double strength, int lifetime) {
        if (level.isClientSide || strength <= 0.0) return;

        Deque<Stimulus> list = LEVELS.computeIfAbsent(level.dimension(), k -> new ArrayDeque<>());
        int cap = UndyingConfig.CFG.maxStimuliPerLevel.get();

        // Drop the oldest rather than growing without bound.
        while (list.size() >= cap) {
            list.pollFirst();
        }
        list.addLast(new Stimulus(pos, type, strength, level.getGameTime(), lifetime));
    }

    /** Called once per server tick per level. Cheap: only trims the expired head. */
    public static void tick(Level level) {
        Deque<Stimulus> list = LEVELS.get(level.dimension());
        if (list == null || list.isEmpty()) return;

        final long now = level.getGameTime();
        while (!list.isEmpty() && list.peekFirst().isDead(now)) {
            list.pollFirst();
        }

        // Lifetimes differ per sense, so a long-lived scent sitting at the head
        // hides dead sounds queued behind it from the trim above. Sweep the rest
        // occasionally — the deque is capped, so the cost is bounded.
        if (now % SWEEP_INTERVAL == 0 && !list.isEmpty()) {
            list.removeIf(s -> s.isDead(now));
        }
    }

    /**
     * Strongest stimulus perceivable from this point, or null.
     * This is the hot path — keep it allocation-free.
     */
    public static Stimulus strongestNear(Level level, Vec3 observer, double range) {
        Deque<Stimulus> list = LEVELS.get(level.dimension());
        if (list == null || list.isEmpty()) return null;

        long now = level.getGameTime();
        Stimulus best = null;
        double bestWeight = 0.0;

        for (Stimulus s : list) {
            if (s.isDead(now)) continue;
            double w = s.weightFrom(observer, now, range);
            if (w > bestWeight) {
                bestWeight = w;
                best = s;
            }
        }
        return best;
    }

    public static void clear(Level level) {
        LEVELS.remove(level.dimension());
    }

    public static void clearAll() {
        LEVELS.clear();
    }
}
