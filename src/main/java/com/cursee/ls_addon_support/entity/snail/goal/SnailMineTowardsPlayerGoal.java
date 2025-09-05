package com.cursee.ls_addon_support.entity.snail.goal;


import com.cursee.ls_addon_support.entity.snail.Snail;
import org.jetbrains.annotations.NotNull;

public final class SnailMineTowardsPlayerGoal extends SnailFlyGoal {

  public SnailMineTowardsPlayerGoal(@NotNull Snail mob) {
    super(mob);
  }

  @Override
  public boolean canStart() {
      if (getMob().isPaused()) {
          return false;
      }

    if (getMob().getBoundPlayer() == null) {
      return false;
    }

    if (getMob().getNavigation().getCurrentPath() == null) {
      return false;
    }

    if (!getMob().getNavigation().getCurrentPath().isFinished()) {
      return false;
    }

    boolean canWalk = getMob().canPathToPlayer(false);
    boolean canFly = getMob().canPathToPlayer(true);

    return !canWalk && !canFly;
  }

  @Override
  public boolean shouldContinue() {
    if (getMob().getBoundPlayer() == null) {
      return false;
    }

    boolean canWalk = getMob().canPathToPlayer(false);
    boolean canFly = getMob().canPathToPlayer(true);

    return !canWalk && !canFly;
  }

  @Override
  public void start() {
    getMob().mining = true;
    getMob().updateNavigation();
  }

  @Override
  public void stop() {
    getMob().mining = false;
    getMob().flying = true;
    getMob().updateNavigation();
  }
}