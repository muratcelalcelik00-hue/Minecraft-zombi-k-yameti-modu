package com.murat.undying.event;

import com.murat.undying.ai.BreakSoftBlockGoal;
import com.murat.undying.ai.SenseInvestigateGoal;
import com.murat.undying.config.UndyingConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.UUID;

/**
 * Everything that makes a vanilla zombie behave like an infected human.
 *
 * Nothing new is registered as a mob. Modifying the vanilla zombie means every
 * naturally spawned zombie, every spawner, and every other mod's zombie logic
 * inherits this for free.
 */
public class ZombieEvents {

    private static final UUID FOLLOW_RANGE_ID = UUID.fromString("d9454787-7d31-4714-ae4c-c9a4a1bbb539");
    private static final UUID SPEED_ID        = UUID.fromString("ccc526f4-394e-473c-97f2-634a24ab1596");
    private static final UUID ATTACK_ID       = UUID.fromString("310cee1f-f24d-454c-a3b2-56123d63fa36");
    private static final UUID KNOCKBACK_ID    = UUID.fromString("20ccf3f9-1963-4fd9-baef-5600dc40a700");

    @SubscribeEvent
    public static void onJoin(EntityJoinLevelEvent e) {
        if (e.getLevel().isClientSide) return;
        if (!(e.getEntity() instanceof Zombie zombie)) return;

        applyAttributes(zombie);

        // Priority 3: breaking in beats wandering but loses to attacking (2).
        zombie.goalSelector.addGoal(3, new BreakSoftBlockGoal(zombie));
        // Priority 5: investigating has to outrank village-wandering (6) and
        // random strolling (7). A goal added at the same priority as a running
        // one cannot take the MOVE flag from it, so 7 would have meant a zombie
        // already strolling never investigates anything.
        zombie.goalSelector.addGoal(5, new SenseInvestigateGoal(zombie));
    }

    private static void applyAttributes(Zombie zombie) {
        multiply(zombie, Attributes.FOLLOW_RANGE, FOLLOW_RANGE_ID, "undying:follow_range",
                UndyingConfig.CFG.followRangeMultiplier.get());
        multiply(zombie, Attributes.MOVEMENT_SPEED, SPEED_ID, "undying:speed",
                UndyingConfig.CFG.speedMultiplier.get());
        multiply(zombie, Attributes.ATTACK_DAMAGE, ATTACK_ID, "undying:attack",
                UndyingConfig.CFG.attackMultiplier.get());
        addFlat(zombie, Attributes.KNOCKBACK_RESISTANCE, KNOCKBACK_ID, "undying:knockback",
                UndyingConfig.CFG.knockbackResistanceBonus.get());
    }

    private static void multiply(Zombie zombie, Attribute attr, UUID id, String name, double multiplier) {
        // MULTIPLY_BASE adds base * amount, so 1.35x is an amount of 0.35.
        apply(zombie, attr, id, name, multiplier - 1.0, AttributeModifier.Operation.MULTIPLY_BASE);
    }

    private static void addFlat(Zombie zombie, Attribute attr, UUID id, String name, double bonus) {
        // The attribute clamps itself to its own range, so knockback resistance
        // cannot be pushed past 1.0 here.
        apply(zombie, attr, id, name, bonus, AttributeModifier.Operation.ADDITION);
    }

    /**
     * Attributes are applied as transient modifiers under a fixed UUID, never by
     * multiplying the base value.
     *
     * Base values are written to the entity's NBT: multiplying one on every join
     * event would compound on every chunk reload, so a zombie that had been
     * loaded ten times would hit for 1.25^10. Transient modifiers are not saved,
     * and the fixed UUID means re-applying replaces instead of stacking.
     */
    private static void apply(Zombie zombie, Attribute attr, UUID id, String name,
                              double amount, AttributeModifier.Operation op) {
        AttributeInstance inst = zombie.getAttribute(attr);
        if (inst == null) return;

        AttributeModifier existing = inst.getModifier(id);
        if (existing != null) {
            if (existing.getAmount() == amount) return;
            inst.removeModifier(id);    // config changed since this zombie last loaded
        }
        if (amount == 0.0) return;      // nothing to apply, and no stale modifier left

        inst.addTransientModifier(new AttributeModifier(id, name, amount, op));
    }

    /**
     * Daylight is not a safe zone.
     *
     * Vanilla sets the zombie on fire every tick it is exposed, so this clears
     * it every tick rather than trying to prevent it. Cheap, and it survives
     * any other mod that also sets zombies alight.
     */
    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent e) {
        if (!UndyingConfig.CFG.immuneToSunlight.get()) return;
        if (!(e.getEntity() instanceof Zombie zombie)) return;
        if (zombie.level().isClientSide) return;
        if (!zombie.isOnFire()) return;

        if (!zombie.level().isDay()) return;
        if (!zombie.level().canSeeSky(zombie.blockPosition())) return;

        // Only cancel sun burn. A zombie standing in lava, fire or on a campfire
        // is burning for a reason the player arranged, so let it burn.
        if (standingInFire(zombie)) return;

        zombie.clearFire();
    }

    private static boolean standingInFire(Zombie zombie) {
        if (zombie.isInLava()) return true;

        BlockPos pos = zombie.blockPosition();
        BlockState at = zombie.level().getBlockState(pos);
        if (at.is(Blocks.FIRE) || at.is(Blocks.SOUL_FIRE) || at.is(Blocks.LAVA)
                || at.is(Blocks.CAMPFIRE) || at.is(Blocks.SOUL_CAMPFIRE)) {
            return true;
        }

        BlockState below = zombie.level().getBlockState(pos.below());
        return below.is(Blocks.MAGMA_BLOCK) || below.is(Blocks.LAVA)
                || below.is(Blocks.CAMPFIRE) || below.is(Blocks.SOUL_CAMPFIRE);
    }
}
