package com.murat.undying.ai;

import com.murat.undying.config.UndyingConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.EnumSet;

/**
 * Zombies pound through the block between them and their target, but only if
 * it is soft enough. Hardness is the whole design: a dirt hut falls, a stone
 * bunker holds. That turns building material into a real decision.
 *
 * Deliberately does NOT place blocks. Zombies stacking cobblestone towers is a
 * gameplay mechanic, not an apocalypse.
 */
public class BreakSoftBlockGoal extends Goal {

    private final Zombie zombie;
    private BlockPos target;
    private int progress;
    private int requiredTicks;

    public BreakSoftBlockGoal(Zombie zombie) {
        this.zombie = zombie;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (!UndyingConfig.CFG.breakingEnabled.get()) return false;

        LivingEntity victim = zombie.getTarget();
        if (victim == null) return false;

        // Only bother when the zombie is stuck: it wants to move but cannot.
        if (!zombie.getNavigation().isDone()) return false;
        if (zombie.distanceToSqr(victim) > 256.0) return false;

        Level level = zombie.level();
        if (UndyingConfig.CFG.respectMobGriefing.get()
                && !level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
            return false;
        }

        BlockPos found = findObstacle();
        if (found == null) return false;

        target = found;
        progress = 0;

        BlockState state = level.getBlockState(target);
        float hardness = state.getDestroySpeed(level, target);
        requiredTicks = Math.max(10,
                (int) (hardness * UndyingConfig.CFG.breakTicksPerHardness.get()));
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        if (target == null) return false;
        if (zombie.getTarget() == null) return false;

        Level level = zombie.level();
        BlockState state = level.getBlockState(target);
        if (state.isAir()) return false;

        return zombie.distanceToSqr(
                target.getX() + 0.5, target.getY() + 0.5, target.getZ() + 0.5) < 9.0;
    }

    @Override
    public void tick() {
        if (target == null) return;

        Level level = zombie.level();
        progress++;

        zombie.getLookControl().setLookAt(
                target.getX() + 0.5, target.getY() + 0.5, target.getZ() + 0.5);

        // Every strike is a sound source. Breaking into a house draws more zombies.
        if (progress % 10 == 0) {
            zombie.swing(zombie.getUsedItemHand());
            level.playSound(null, target, SoundEvents.ZOMBIE_ATTACK_WOODEN_DOOR,
                    SoundSource.HOSTILE, 0.8F, 0.9F + zombie.getRandom().nextFloat() * 0.2F);
            level.levelEvent(2001, target, net.minecraft.world.level.block.Block
                    .getId(level.getBlockState(target)));
        }

        if (progress >= requiredTicks) {
            boolean drop = UndyingConfig.CFG.dropBrokenBlocks.get();
            level.destroyBlock(target, drop, zombie);
            target = null;
            progress = 0;
        }
    }

    @Override
    public void stop() {
        target = null;
        progress = 0;
    }

    /**
     * Looks for a breakable block directly in front of the zombie, at foot and
     * head height. Two candidates only — scanning a volume every tick is what
     * makes block-breaking mods expensive.
     */
    private BlockPos findObstacle() {
        Level level = zombie.level();
        LivingEntity victim = zombie.getTarget();
        if (victim == null) return null;

        double dx = victim.getX() - zombie.getX();
        double dz = victim.getZ() - zombie.getZ();
        double len = Math.sqrt(dx * dx + dz * dz);
        if (len < 0.01) return null;
        dx /= len;
        dz /= len;

        BlockPos foot = BlockPos.containing(
                zombie.getX() + dx, zombie.getY(), zombie.getZ() + dz);
        BlockPos head = foot.above();

        for (BlockPos pos : new BlockPos[]{foot, head}) {
            if (isBreakable(level, pos)) return pos;
        }
        return null;
    }

    private boolean isBreakable(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.isAir()) return false;
        if (state.is(Blocks.BEDROCK)) return false;
        if (state.hasBlockEntity()) return false;   // no chest looting, no lag
        if (state.getFluidState().isSource()) return false;

        float hardness = state.getDestroySpeed(level, pos);
        if (hardness < 0) return false;             // unbreakable
        return hardness <= UndyingConfig.CFG.maxBlockHardness.get();
    }
}
