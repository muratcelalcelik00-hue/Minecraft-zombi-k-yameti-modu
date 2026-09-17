package com.murat.undying.ai;

import com.murat.undying.config.UndyingConfig;

/**
 * Caps how many zombies may compute a fresh path in a single tick.
 *
 * Without this a horde of thirty will pin the server thread on a phone.
 * Zombies denied the budget do not disappear or freeze — they simply keep
 * their last path for another tick, which is invisible to the player.
 */
public final class PathingBudget {

    private static int used = 0;

    private PathingBudget() {}

    public static void resetTick() {
        used = 0;
    }

    public static boolean tryAcquire() {
        if (used >= UndyingConfig.CFG.pathingBudgetPerTick.get()) {
            return false;
        }
        used++;
        return true;
    }
}
