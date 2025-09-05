package com.cursee.ls_addon_support.utils.world;

import com.cursee.ls_addon_support.network.NetworkHandlerServer;
import com.cursee.ls_addon_support.utils.enums.PacketNames;
import com.cursee.ls_addon_support.utils.other.TaskScheduler;
import com.cursee.ls_addon_support.utils.player.PlayerUtils;
import java.awt.Color;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.joml.Vector3f;

//? if >= 1.21.2 {
/*import java.awt.Color;
 *///?}

public class AnimationUtils {

  private static int spiralDuration = 175;

  public static void playTotemAnimation(ServerPlayerEntity player) {
    //The animation lasts about 40 ticks.
    player.networkHandler.sendPacket(new EntityStatusS2CPacket(player, (byte) 35));
  }

  public static void playRealTotemAnimation(ServerPlayerEntity player) {
    // Visible by other players too
    PlayerUtils.getServerWorld(player).sendEntityStatus(player, (byte) 35);
  }

  public static void playSecretLifeTotemAnimation(ServerPlayerEntity player, boolean red) {
    if (NetworkHandlerServer.wasHandshakeSuccessful(player)) {
      NetworkHandlerServer.sendStringPacket(player, PacketNames.SHOW_TOTEM,
          red ? "task_red" : "task");
      PlayerUtils.playSoundToPlayer(player, SoundEvent.of(Identifier.of("secretlife_task_totem")));
      return;
    }

    ItemStack totemItem = getSecretLifeTotemItem(red);
    ItemStack mainhandItem = player.getMainHandStack().copy();
    player.setStackInHand(Hand.MAIN_HAND, totemItem);
    TaskScheduler.scheduleTask(1, () -> {
      player.networkHandler.sendPacket(new EntityStatusS2CPacket(player, (byte) 35));
      PlayerUtils.playSoundToPlayer(player, SoundEvent.of(Identifier.of("secretlife_task_totem")));
    });
    TaskScheduler.scheduleTask(2, () -> {
      player.setStackInHand(Hand.MAIN_HAND, mainhandItem);
    });
  }

  public static ItemStack getSecretLifeTotemItem(boolean red) {
    ItemStack totemItem = Items.TOTEM_OF_UNDYING.getDefaultStack();
    ItemStackUtils.setCustomComponentBoolean(totemItem, "FakeTotem", true);
    totemItem.set(DataComponentTypes.ITEM_MODEL, Identifier.of("lifeseries",red ? "task_red_totem" : "task_totem"));
    return totemItem;
  }

  public static void createSpiral(ServerPlayerEntity player, int duration) {
    spiralDuration = duration;
    TaskScheduler.scheduleTask(1, () -> startSpiral(player));
  }

  private static void startSpiral(ServerPlayerEntity player) {
    TaskScheduler.scheduleTask(1, () -> runSpiralStep(player, 0));
  }

  private static void runSpiralStep(ServerPlayerEntity player, int step) {
      if (player == null) {
          return;
      }

    processSpiral(player, step);
    processSpiral(player, step + 1);
    processSpiral(player, step + 2);
    processSpiral(player, step + 3);

    if (step <= spiralDuration) {
      TaskScheduler.scheduleTask(1, () -> runSpiralStep(player, step + 4));
    }
  }

  private static void processSpiral(ServerPlayerEntity player, int step) {
    ServerWorld world = PlayerUtils.getServerWorld(player);
    double x = player.getX();
    double z = player.getZ();
    double yStart = player.getY();
    double height = 1.0;
    double radius = 0.8;
    int pointsPerCircle = 40;

    double angle = 2 * Math.PI * (step % pointsPerCircle) / pointsPerCircle + step / 4.0;
    double y = yStart + height * Math.sin(Math.PI * (step - 20) / 20) + 1;

    double offsetX = radius * Math.cos((float) angle);
    double offsetZ = radius * Math.sin((float) angle);

    world.spawnParticles(
        ParticleTypes.HAPPY_VILLAGER,
        x + offsetX, y, z + offsetZ,
        1, 0, 0, 0, 0
    );
  }

  public static void createGlyphAnimation(ServerWorld world, Vec3d target, int duration) {
      if (world == null || target == null || duration <= 0) {
          return;
      }

    double radius = 7.5; // Radius of the glyph starting positions

    for (int step = 0; step < duration; step++) {
      TaskScheduler.scheduleTask(step, () -> spawnGlyphParticles(world, target, radius));
    }
  }

  private static void spawnGlyphParticles(ServerWorld world, Vec3d target, double radius) {
    int particlesPerTick = 50; // Number of glyphs spawned per tick
    Random random = world.getRandom();

    for (int i = 0; i < particlesPerTick; i++) {
      // Randomize starting position around the target block
      double angle = random.nextDouble() * 2 * Math.PI;
      double distance = radius * (random.nextDouble() * 0.5); // Random distance within radius

      double startX = target.getX() + distance * Math.cos(angle);
      double startY = target.getY() + random.nextDouble() * 2 + 1; // Random height variation
      double startZ = target.getZ() + distance * Math.sin(angle);

      // Compute the velocity vector toward the target
      double targetX = target.getX();
      double targetY = target.getY();
      double targetZ = target.getZ();

      double dx = targetX - startX;
      double dy = targetY - startY;
      double dz = targetZ - startZ;

      // Normalize velocity to control particle speed
      double velocityScale = -50; // Adjust speed (lower values for slower movement)
      double vx = dx * velocityScale;
      double vy = dy * velocityScale;
      double vz = dz * velocityScale;

      // Spawn the particle with velocity
      world.spawnParticles(
          ParticleTypes.ENCHANT, // Glyph particle
          startX, startY, startZ, // Starting position
          0, // Number of particles to display as a burst (keep 0 for velocity to work)
          vx, vy, vz, // Velocity components
          0.2 // Spread (keep non-zero to activate velocity)
      );
    }
  }

  public static void spawnFireworkBall(ServerWorld world, Vec3d position, int duration,
      double radius, Vector3f color) {
      if (world == null || position == null || duration <= 0 || radius <= 0) {
          return;
      }

    Random random = world.getRandom();

    for (int step = 0; step < duration; step++) {
      TaskScheduler.scheduleTask(step, () -> {
        // Spawn particles in a spherical pattern for the current step
        for (int i = 0; i < 50; i++) { // 50 particles per tick
          double theta = random.nextDouble() * 2 * Math.PI; // Angle in the XY plane
          double phi = random.nextDouble() * Math.PI; // Angle from the Z-axis
          double r = radius * (0.8 + 0.2 * random.nextDouble()); // Slight variation in radius

          // Spherical coordinates to Cartesian
          double x = r * Math.sin(phi) * Math.cos(theta);
          double y = r * Math.sin(phi) * Math.sin(theta);
          double z = r * Math.cos(phi);

          // Create the particle effect with the generated color and size
          DustParticleEffect particleEffect = new DustParticleEffect(new Color(color.x, color.y, color.z).getRGB(), 1.0f);

          // Spawn particle with random offset
          world.spawnParticles(
              particleEffect, // Colored particle effect
              position.getX() + x,
              position.getY() + y,
              position.getZ() + z,
              1, // Particle count
              0, 0, 0, // No velocity
              0 // No spread
          );
        }
      });
    }
  }

  public static void spawnTeleportParticles(ServerWorld world, Vec3d pos) {
    world.spawnParticles(
        ParticleTypes.PORTAL,
        pos.x, pos.y, pos.z,
        30,
        0, 0, 0,
        0.35
    );
  }
}
