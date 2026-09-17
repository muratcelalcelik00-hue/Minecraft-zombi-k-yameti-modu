package com.murat.undying.ai;

import com.murat.undying.config.UndyingConfig;
import com.murat.undying.sense.SenseManager;
import com.murat.undying.sense.Stimulus;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

/**
 * The core of the mod.
 *
 * A zombie with no target walks toward whatever it can hear, smell or see the
 * glow of. It never gains knowledge of where you actually are — it only knows
 * where something happened. Vanilla targeting still needs line of sight, so the
 * zombie has to physically arrive and look at you to lock on.
 */
public class SenseInvestigateGoal extends Goal {

    private final Zombie zombie;
    private Vec3 destination;
    private int ticksSpent;
    private int cooldown;
    private int ticksUntilCheck = -1;

    public SenseInvestigateGoal(Zombie zombie) {
        this.zombie = zombie;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (cooldown > 0) {
            cooldown--;
            return false;
        }

        // A zombie that can already see you does not need to investigate.
        if (zombie.getTarget() != null) return false;

        // Stagger the query so the horde never polls on the same tick. A private
        // countdown, seeded randomly per zombie — keying this off game time
        // instead would tie it to the parity of the goal selector's own every-
        // other-tick poll, and could stop the whole system from ever running.
        int interval = UndyingConfig.CFG.senseCheckInterval.get();
        if (ticksUntilCheck < 0) {
            ticksUntilCheck = zombie.getRandom().nextInt(interval);
        }
        if (ticksUntilCheck > 0) {
            ticksUntilCheck--;
            return false;
        }
        ticksUntilCheck = interval;

        Stimulus s = SenseManager.strongestNear(
                zombie.level(),
                zombie.position(),
                UndyingConfig.CFG.senseRange.get()
        );
        if (s == null) return false;

        destination = s.pos;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        if (destination == null) return false;
        if (zombie.getTarget() != null) return false;
        if (ticksSpent > UndyingConfig.CFG.investigateGiveUpTicks.get()) return false;
        return zombie.distanceToSqr(destination) > 4.0;
    }

    @Override
    public void start() {
        ticksSpent = 0;
        pushPath();
    }

    @Override
    public void stop() {
        destination = null;
        ticksSpent = 0;
        // Brief pause so a zombie that just failed does not re-query every tick.
        cooldown = 40;
        zombie.getNavigation().stop();
    }

    @Override
    public void tick() {
        ticksSpent++;
        if (destination == null) return;

        zombie.getLookControl().setLookAt(destination.x, destination.y, destination.z);

        // Recompute rarely, and only if the budget allows it.
        if (ticksSpent % 20 == 0 && zombie.getNavigation().isDone()) {
            pushPath();
        }
    }

    private void pushPath() {
        if (!PathingBudget.tryAcquire()) return;
        zombie.getNavigation().moveTo(destination.x, destination.y, destination.z, 1.0D);
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return false;
    }
}
