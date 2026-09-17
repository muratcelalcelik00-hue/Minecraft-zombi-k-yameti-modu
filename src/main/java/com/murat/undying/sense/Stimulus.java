package com.murat.undying.sense;

import net.minecraft.world.phys.Vec3;

/**
 * One perceivable event in the world. Decays on its own; nothing owns it.
 */
public class Stimulus {

    public final Vec3 pos;
    public final SenseType type;
    public final double strength;
    public final long bornTick;
    public final long deathTick;

    public Stimulus(Vec3 pos, SenseType type, double strength, long bornTick, int lifetime) {
        this.pos = pos;
        this.type = type;
        this.strength = strength;
        this.bornTick = bornTick;
        this.deathTick = bornTick + lifetime;
    }

    public boolean isDead(long now) {
        return now >= deathTick;
    }

    /** Linear decay to zero over the stimulus lifetime. */
    public double currentStrength(long now) {
        if (isDead(now)) return 0.0;
        double total = deathTick - bornTick;
        double left = deathTick - now;
        return strength * (left / total);
    }

    /**
     * How loud this reads from a given point. Falls off with distance so a
     * nearby footstep can still lose to a distant explosion.
     */
    public double weightFrom(Vec3 observer, long now, double maxRange) {
        double distSqr = pos.distanceToSqr(observer);
        if (distSqr > maxRange * maxRange) return 0.0;
        return currentStrength(now) / (1.0 + distSqr * 0.01);
    }
}
