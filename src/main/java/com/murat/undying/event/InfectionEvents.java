package com.murat.undying.event;

import com.murat.undying.config.UndyingConfig;
import com.murat.undying.infection.ModEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * The bite, and what it costs.
 *
 * Every zombie in the world was a person. Making the player's own death produce
 * one more is what closes that loop — and it is the moment the apocalypse stops
 * being scenery and starts being about you.
 */
public class InfectionEvents {

    @SubscribeEvent
    public static void onHurt(LivingHurtEvent e) {
        if (!UndyingConfig.CFG.infectionEnabled.get()) return;

        LivingEntity victim = e.getEntity();
        if (victim.level().isClientSide) return;
        if (!(e.getSource().getEntity() instanceof Zombie)) return;

        boolean isPlayer = victim instanceof Player;
        boolean isVillager = victim instanceof Villager;
        if (!isPlayer && !(isVillager && UndyingConfig.CFG.infectVillagers.get())) return;

        if (victim.hasEffect(ModEffects.INFECTION.get())) return;

        if (victim.getRandom().nextDouble() >= UndyingConfig.CFG.infectionChance.get()) return;

        victim.addEffect(new MobEffectInstance(
                ModEffects.INFECTION.get(),
                UndyingConfig.CFG.infectionDurationTicks.get(),
                0, false, true, true
        ));
    }

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent e) {
        if (!UndyingConfig.CFG.infectionEnabled.get()) return;
        if (!UndyingConfig.CFG.reanimateOnDeath.get()) return;

        LivingEntity victim = e.getEntity();
        Level level = victim.level();
        if (level.isClientSide) return;
        if (!(level instanceof ServerLevelAccessor accessor)) return;

        if (!victim.hasEffect(ModEffects.INFECTION.get())) return;

        BlockPos pos = victim.blockPosition();
        Zombie risen = EntityType.ZOMBIE.create(level);
        if (risen == null) return;

        risen.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5,
                victim.getYRot(), 0.0F);
        risen.finalizeSpawn(accessor, level.getCurrentDifficultyAt(pos),
                MobSpawnType.CONVERSION, null, null);

        // It wears the face of who it was, and it does not despawn: a zombie
        // carrying your name has to still be there when you come back for it.
        if (victim instanceof Player player) {
            risen.setCustomName(player.getName());
            risen.setCustomNameVisible(false);
            risen.setPersistenceRequired();
        }

        level.addFreshEntity(risen);
    }
}
