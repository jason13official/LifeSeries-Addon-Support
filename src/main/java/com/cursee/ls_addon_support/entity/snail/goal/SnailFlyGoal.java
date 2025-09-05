package com.cursee.ls_addon_support.entity.snail.goal;


import com.cursee.ls_addon_support.entity.snail.Snail;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import org.jetbrains.annotations.NotNull;

public class SnailFlyGoal extends Goal {

  @NotNull
  private final Snail mob;

  public SnailFlyGoal(@NotNull Snail mob) {
    this.mob = mob;
  }

  @NotNull
  protected Snail getMob() {
    return this.mob;
  }

  @Override
  public boolean canStart() {
      if (mob.isPaused()) {
          return false;
      }
    if (!mob.flying || mob.gliding) {
      return false;
    }

    LivingEntity boundPlayer = mob.getBoundPlayer();
    if (boundPlayer == null) {
      return false;
    }

    return getMob().canPathToPlayer(true);
  }

  @Override
  public boolean shouldContinue() {
      if (!mob.flying) {
          return false;
      }
    return mob.getBoundPlayer() != null;
  }

  @Override
  public void start() {
  }

  @Override
  public void stop() {
    mob.flying = false;
    mob.updateNavigation();
    mob.updateMoveControl();
  }
}