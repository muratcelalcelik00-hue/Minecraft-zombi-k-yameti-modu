package com.murat.undying.infection;

import com.murat.undying.config.UndyingConfig;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

/**
 * A wound that does not heal.
 *
 * Instead of ticking damage, infection eats max health as it progresses. The
 * player watches their heart bar shrink and has to decide whether to run for a
 * cure or make peace with it. Damage over time can be out-healed; this cannot.
 */
public class InfectionEffect extends MobEffect {

    public InfectionEffect() {
        super(MobEffectCategory.HARMFUL, 0x4A6B3A);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide) return;

        var inst = entity.getEffect(this);
        if (inst == null) return;

        int total = UndyingConfig.CFG.infectionDurationTicks.get();
        int elapsed = total - inst.getDuration();
        double progress = Math.min(1.0, (double) elapsed / total);

        double maxLoss = UndyingConfig.CFG.infectionMaxHealthLoss.get();
        float ceiling = (float) Math.max(2.0, entity.getMaxHealth() - maxLoss * progress);

        if (entity.getHealth() > ceiling) {
            entity.setHealth(ceiling);
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        // Once a second is plenty; this does not need to run 20x.
        return duration % 20 == 0;
    }
}
