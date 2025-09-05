package com.cursee.ls_addon_support.entity.pathfinder;

import com.cursee.ls_addon_support.LSAddonSupport;
import de.tomalbrc.bil.api.AnimatedEntity;
import de.tomalbrc.bil.api.AnimatedEntityHolder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.AmbientEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class PathFinder extends AmbientEntity implements AnimatedEntity {

  public static final Identifier ID = Identifier.of(LSAddonSupport.MOD_ID, "pathfinder");
  public static final float MOVEMENT_SPEED = 0.35f;
  public static final float FLYING_SPEED = 0.3f;
  private int despawnTimer = 0;

  public PathFinder(EntityType<? extends AmbientEntity> entityType, World world) {
    super(entityType, world);
    setInvulnerable(true);
    setNoGravity(true);
    setPersistent();
    setInvisible(true);
    noClip = true;
  }

  public static DefaultAttributeContainer.Builder createAttributes() {
    return MobEntity.createMobAttributes()
        .add(EntityAttributes.MAX_HEALTH, 10000)
        .add(EntityAttributes.MOVEMENT_SPEED, MOVEMENT_SPEED)
        .add(EntityAttributes.FLYING_SPEED, FLYING_SPEED)
        .add(EntityAttributes.STEP_HEIGHT, 1)
        .add(EntityAttributes.FOLLOW_RANGE, 150)
        .add(EntityAttributes.ATTACK_DAMAGE, 20);
  }

  @Override
  public AnimatedEntityHolder getHolder() {
    return null;
  }

  @Override
  public void tick() {
    setOnGround(true);
    despawnTimer++;
    if (despawnTimer > 100) {
      discard();
    }
  }

  public void setNavigation(boolean flying) {
    despawnTimer = 0;
    setPathfindingPenalty(PathNodeType.BLOCKED, -1);
    setPathfindingPenalty(PathNodeType.TRAPDOOR, -1);
    setPathfindingPenalty(PathNodeType.DANGER_TRAPDOOR, -1);
    setPathfindingPenalty(PathNodeType.WALKABLE_DOOR, -1);
    setPathfindingPenalty(PathNodeType.DOOR_OPEN, -1);
    setPathfindingPenalty(PathNodeType.UNPASSABLE_RAIL, 0);
    if (flying) {
      moveControl = new FlightMoveControl(this, 20, true);
      navigation = new BirdNavigation(this, getWorld());
      navigation.setCanSwim(true);
    } else {
      moveControl = new MoveControl(this);
      navigation = new MobNavigation(this, getWorld());
      navigation.setCanSwim(true);
    }
  }

  public boolean canPathfind(Entity pathfindTo, boolean flying) {
    despawnTimer = 0;
      if (pathfindTo == null) {
          return false;
      }
    setNavigation(flying);
    Path path = navigation.findPathTo(pathfindTo, 0);
      if (path == null) {
          return false;
      }
    PathNode end = path.getEnd();
      if (end == null) {
          return false;
      }
    return end.getBlockPos().equals(pathfindTo.getBlockPos());
  }

  public void resetDespawnTimer() {
    despawnTimer = 0;
  }

  @Override
  public boolean isPushable() {
    return false;
  }
}
