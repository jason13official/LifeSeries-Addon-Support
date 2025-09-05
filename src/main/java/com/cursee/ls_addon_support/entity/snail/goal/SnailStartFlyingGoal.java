package com.cursee.ls_addon_support.entity.snail.goal;

import com.cursee.ls_addon_support.entity.snail.Snail;
import net.minecraft.entity.ai.goal.Goal;
import org.jetbrains.annotations.NotNull;

public final class SnailStartFlyingGoal extends Goal {

  @NotNull
  private final Snail mob;
  private final int startFlyingDelay = 70;
  private int startFlyingCounter;
  private boolean canWalk = true;
  private boolean canFly = true;

  public SnailStartFlyingGoal(@NotNull Snail mob) {
    this.mob = mob;
  }

  @Override
  public boolean canStart() {
      if (mob.isPaused()) {
          return false;
      }
    if (mob.getBoundPlayer() == null) {
      return false;
    }

    if (mob.flying) {
      return false;
    }

        /*
        if (!mob.isTargetOnGround()) {
            return false;
        }*/

    if (mob.getNavigation().getCurrentPath() == null) {
      return false;
    }

    canWalk = mob.canPathToPlayer(false);
    canFly = mob.canPathToPlayer(true);

    if (canWalk) {
      startFlyingCounter = 0;
    } else if (canFly) {
      startFlyingCounter++;
    }

    return startFlyingCounter >= startFlyingDelay;
  }

  @Override
  public void start() {
    mob.flying = true;
    mob.updateNavigation();
    mob.updateMoveControl();
    mob.playStartFlyAnimation();
  }

  @Override
  public void stop() {
    startFlyingCounter = 0;
    canWalk = true;
  }
}