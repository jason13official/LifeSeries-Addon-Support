package com.cursee.ls_addon_support.entity.triviabot.goal;

import com.cursee.ls_addon_support.entity.triviabot.TriviaBot;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

public final class TriviaBotTeleportGoal extends Goal {

  @NotNull
  private final TriviaBot mob;
  private final int maxTicksSinceLastPositionChange;
  private int teleportCooldown = 0;
  @NotNull
  private BlockPos lastPosition;
  private int ticksSinceLastPositionChange;

  public TriviaBotTeleportGoal(@NotNull TriviaBot mob) {
    this.mob = mob;
    this.maxTicksSinceLastPositionChange = TriviaBot.STATIONARY_TP_COOLDOWN;
    this.lastPosition = BlockPos.ORIGIN;
  }

  @Override
  public boolean canStart() {
    if (mob.interactedWith) {
      return false;
    }
    if (teleportCooldown > 0) {
      teleportCooldown--;
      return false;
    }

    if (mob.getBoundPlayer() == null) {
      return false;
    }
    ServerPlayerEntity boundPlayer = mob.getBoundPlayer();

    float distFromPlayer = mob.distanceTo(boundPlayer);
      if (distFromPlayer > TriviaBot.MAX_DISTANCE) {
          return true;
      }

    if (!mob.getBlockPos().equals(this.lastPosition) || distFromPlayer < 4) {
      this.ticksSinceLastPositionChange = 0;
      this.lastPosition = mob.getBlockPos();
    }

    this.ticksSinceLastPositionChange++;
      if (this.ticksSinceLastPositionChange > this.maxTicksSinceLastPositionChange) {
          return true;
      }

    boolean dimensionsAreSame = mob.getWorld().getRegistryKey()
        .equals(boundPlayer.getWorld().getRegistryKey());
    return !dimensionsAreSame;
  }

  @Override
  public void start() {
    teleportCooldown = 20;
    mob.fakeTeleportToPlayer();
  }

  @Override
  public boolean shouldContinue() {
    return false;
  }
}