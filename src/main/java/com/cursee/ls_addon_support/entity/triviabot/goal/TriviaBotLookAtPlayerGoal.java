package com.cursee.ls_addon_support.entity.triviabot.goal;

import com.cursee.ls_addon_support.entity.triviabot.TriviaBot;
import java.util.EnumSet;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

public class TriviaBotLookAtPlayerGoal extends Goal {

  protected static final double RANGE_SQUARED = 400;
  protected final TriviaBot mob;
  @Nullable
  protected ServerPlayerEntity target;
  private int lookTime;

  public TriviaBotLookAtPlayerGoal(TriviaBot mob) {
    this.mob = mob;
    this.setControls(EnumSet.of(Control.LOOK));
  }

  public boolean canStart() {
      if (!mob.interactedWith) {
          return false;
      }

    target = mob.getBoundPlayer();
      if (target == null) {
          return false;
      }

    return this.mob.squaredDistanceTo(this.target) <= RANGE_SQUARED;
  }

  @Override
  public boolean shouldContinue() {
      if (this.target == null) {
          return false;
      }
      if (!this.target.isAlive()) {
          return false;
      }
      if (this.mob.squaredDistanceTo(this.target) > RANGE_SQUARED) {
          return false;
      }
    return this.lookTime > 0;
  }

  @Override
  public void start() {
    this.lookTime = this.getTickCount(40 + this.mob.getRandom().nextInt(40));
  }

  @Override
  public void stop() {
    this.target = null;
  }

  @Override
  public void tick() {
    if (this.target != null && this.target.isAlive()) {
      double d = this.mob.getEyeY();
      this.mob.getLookControl().lookAt(this.target.getX(), d, this.target.getZ());
      --this.lookTime;
    }
  }
}
