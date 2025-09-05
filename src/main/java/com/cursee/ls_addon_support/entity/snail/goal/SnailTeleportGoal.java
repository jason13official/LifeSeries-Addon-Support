package com.cursee.ls_addon_support.entity.snail.goal;


import com.cursee.ls_addon_support.entity.snail.Snail;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

public final class SnailTeleportGoal extends Goal {


  @NotNull
  private final Snail mob;
  private final int maxTicksSinceLastPositionChange;
  private int ticksSinceLastPositionChange;
  private int teleportCooldown = 0;
  @NotNull
  private BlockPos lastPosition;

  public SnailTeleportGoal(@NotNull Snail mob) {
    this.mob = mob;
    this.maxTicksSinceLastPositionChange = Snail.STATIONARY_TP_COOLDOWN;
    this.lastPosition = BlockPos.ORIGIN;
  }

  @Override
  public boolean canStart() {
      if (mob.isPaused()) {
          return false;
      }
    if (teleportCooldown > 0) {
      teleportCooldown--;
      return false;
    }
    if (mob.getBoundPlayer() == null) {
      return false;
    }
    if (!mob.getBlockPos().equals(this.lastPosition)) {
      this.ticksSinceLastPositionChange = 0;
      this.lastPosition = mob.getBlockPos();
    }

    this.ticksSinceLastPositionChange++;

    ServerPlayerEntity boundPlayer = mob.getBoundPlayer();
    float distFromPlayer = mob.distanceTo(boundPlayer);
    boolean dimensionsAreSame = mob.getWorld().getRegistryKey()
        .equals(boundPlayer.getWorld().getRegistryKey());
    return !dimensionsAreSame || distFromPlayer > Snail.MAX_DISTANCE
        || this.ticksSinceLastPositionChange > this.maxTicksSinceLastPositionChange;
  }

  @Override
  public void start() {
    teleportCooldown = 20;
    mob.fakeTeleportNearPlayer(Snail.TP_MIN_RANGE);
  }

  @Override
  public boolean shouldContinue() {
    return false;
  }
}