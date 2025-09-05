package com.cursee.ls_addon_support.entity.snail.goal;

import com.cursee.ls_addon_support.entity.snail.Snail;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

public final class SnailJumpAttackPlayerGoal extends Goal {

  @NotNull
  private final Snail mob;
  @NotNull
  private Vec3d previousTargetPosition = Vec3d.ZERO;
  private int attackCooldown = Snail.JUMP_COOLDOWN_SHORT;
  private int attackCooldown2 = 0;

  public SnailJumpAttackPlayerGoal(@NotNull Snail mob) {
    this.mob = mob;
  }

  @Override
  public boolean canStart() {
      if (mob.isPaused()) {
          return false;
      }
    if (mob.dontAttackFor > 0) {
      return false;
    }

    if (mob.gliding || mob.mining) {
      return false;
    }

    ServerPlayerEntity boundPlayer = mob.getBoundPlayer();
    if (boundPlayer == null) {
      return false;
    }

    if (mob.attacking) {
      return true;
    }

    double distanceToTarget = mob.squaredDistanceTo(boundPlayer);
    if (distanceToTarget > mob.getJumpRangeSquared()) {
      return false;
    }

    return mob.canSee(boundPlayer);
  }

  @Override
  public boolean shouldContinue() {
    if (attackCooldown2 > 0) {
      attackCooldown2--;
      return false;
    }

    if (attackCooldown <= 4) {
      return true;
    }

    ServerPlayerEntity boundPlayer = mob.getBoundPlayer();
    if (boundPlayer == null) {
      return false;
    }

    if (mob.squaredDistanceTo(boundPlayer) > mob.getJumpRangeSquared()) {
      return false;
    }

    return mob.canSee(boundPlayer);
  }

  @Override
  public void start() {
    ServerPlayerEntity boundPlayer = mob.getBoundPlayer();
    if (boundPlayer != null) {
      this.previousTargetPosition = boundPlayer.getPos();
    }
    this.attackCooldown = Snail.JUMP_COOLDOWN_SHORT;
    mob.attacking = true;
  }

  @Override
  public void stop() {
    this.attackCooldown = Snail.JUMP_COOLDOWN_SHORT;
    this.previousTargetPosition = Vec3d.ZERO;
    mob.attacking = false;
  }

  @Override
  public void tick() {
    if (attackCooldown2 > 0) {
      attackCooldown2--;
      return;
    }

    ServerPlayerEntity boundPlayer = mob.getBoundPlayer();
    if (attackCooldown > 0) {
      attackCooldown--;
    }
    if (attackCooldown == 4) {
      mob.playAttackSound();
    }
    if (attackCooldown <= 0) {
      jumpAttackPlayer();
    }

    if (boundPlayer != null) {
      this.previousTargetPosition = boundPlayer.getPos();
      mob.lookAtEntity(boundPlayer, 15, 15);
    }
  }

  private void jumpAttackPlayer() {
    ServerPlayerEntity boundPlayer = mob.getBoundPlayer();
    if (boundPlayer == null) {
      return;
    }
    this.attackCooldown = Snail.JUMP_COOLDOWN_SHORT;
    this.attackCooldown2 = Snail.JUMP_COOLDOWN_LONG;

    Vec3d mobVelocity = mob.getVelocity();
    Vec3d relativeTargetPos = new Vec3d(
        previousTargetPosition.getX() - mob.getX(),
        previousTargetPosition.getY() - mob.getY(),
        previousTargetPosition.getZ() - mob.getZ()
    );

    if (boundPlayer.getRandom().nextInt(3) == 0) {
      //Harder attack variant
      relativeTargetPos = new Vec3d(
          boundPlayer.getX() - mob.getX(),
          boundPlayer.getY() - mob.getY(),
          boundPlayer.getZ() - mob.getZ()
      );
    }

    if (boundPlayer.getRandom().nextInt(6) == 0) {
      //EVEN harder attack variant
      Vec3d targetVelocity = boundPlayer.getPos().subtract(previousTargetPosition);
      relativeTargetPos = relativeTargetPos.add(targetVelocity.multiply(3));
    }

    Vec3d attackVector = mobVelocity;
    if (relativeTargetPos.lengthSquared() > 0.0001) {
      attackVector = relativeTargetPos.normalize().multiply(mob.isNerfed() ? 0.8 : 1);
    }
      if (mob.flying) {
          attackVector = attackVector.multiply(0.5);
      }
    double addY = 0.5 + mob.squaredDistanceTo(boundPlayer) / mob.getJumpRangeSquared();
    mob.setVelocity(attackVector.x, attackVector.y + addY, attackVector.z);
  }
}