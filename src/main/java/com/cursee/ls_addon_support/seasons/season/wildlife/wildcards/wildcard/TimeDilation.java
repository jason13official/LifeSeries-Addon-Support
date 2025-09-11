package com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard;

import static com.cursee.ls_addon_support.LSAddonSupport.currentSession;
import static com.cursee.ls_addon_support.LSAddonSupport.server;

import com.cursee.ls_addon_support.network.NetworkHandlerServer;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.Wildcard;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.WildcardManager;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.Wildcards;
import com.cursee.ls_addon_support.utils.enums.PacketNames;
import com.cursee.ls_addon_support.utils.other.OtherUtils;
import com.cursee.ls_addon_support.utils.other.TaskScheduler;
import com.cursee.ls_addon_support.utils.player.PlayerUtils;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.server.ServerTickManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameRules;

public class TimeDilation extends Wildcard {

  public static float MIN_TICK_RATE = 1;
  public static float NORMAL_TICK_RATE = 20;
  public static float MAX_TICK_RATE = 100;

  public static float MIN_TICK_RATE_NERFED = 10;
  public static float MAX_TICK_RATE_NERFED = 30;

  public static float MIN_PLAYER_MSPT = 25.0F;

  public static int updateRate = 100;
  public static int lastDiv = -1;
  public static int activatedAt = -1;
  public static float weatherTicksBacklog = 0;

  public static void slowlySetWorldSpeed(float rate, int ticks) {
      if (server == null) {
          return;
      }
    ServerTickManager serverTickManager = server.getTickManager();
    float currentRate = serverTickManager.getTickRate();
    float step = (rate - currentRate) / (ticks);
    for (int i = 0; i < ticks; i++) {
      int finalI = i;
      TaskScheduler.scheduleTask(i,
          () -> serverTickManager.setTickRate(currentRate + (step * finalI)));
    }
    TaskScheduler.scheduleTask(ticks + 1, () -> serverTickManager.setTickRate(rate));
  }

  public static float getWorldSpeed() {
      if (server == null) {
          return 20;
      }
    ServerTickManager serverTickManager = server.getTickManager();
    return serverTickManager.getTickRate();
  }

  public static void setWorldSpeed(float rate) {
      if (server == null) {
          return;
      }
    ServerTickManager serverTickManager = server.getTickManager();
    serverTickManager.setTickRate(rate);
  }

  private static void adjustCreeperFuseTimes() {
      if (server == null) {
          return;
      }
    ServerTickManager serverTickManager = server.getTickManager();
    float tickRate = serverTickManager.getTickRate();
    short fuseTime = (short) (20 * (tickRate / 20.0f));
    OtherUtils.executeCommand(
        "/execute as @e[type=minecraft:creeper] run data modify entity @s Fuse set value "
            + fuseTime + "s");
  }

  public static float getMaxTickRate() {
      if (isNerfed()) {
          return MAX_TICK_RATE_NERFED;
      }
    return MAX_TICK_RATE;
  }

  public static float getMinTickRate() {
      if (isNerfed()) {
          return MIN_TICK_RATE_NERFED;
      }
    return Math.max(MIN_TICK_RATE, 1);
  }

  public static boolean isNerfed() {
    return WildcardManager.isActiveWildcard(Wildcards.CALLBACK);
  }

  @Override
  public String getId() {
    return Wildcards.TIME_DILATION;
  }

  @Override
  public void tick() {
      if (server == null) {
          return;
      }
    ServerTickManager serverTickManager = server.getTickManager();
    float rate = serverTickManager.getTickRate();
    if (rate > 20) {
      if (rate > 30) {
        adjustCreeperFuseTimes();
      }
      weatherTicksBacklog += (rate - 20) / 2.0f;
      int weatherTicks = (int) weatherTicksBacklog;
      if (weatherTicks >= 1) {
        weatherTicksBacklog -= weatherTicks;
        for (ServerWorld serverWorld : server.getWorlds()) {
          long newTicks = serverWorld.getTimeOfDay() + weatherTicks;
          serverWorld.setTimeOfDay(newTicks);
          for (ServerPlayerEntity player : serverWorld.getPlayers()) {
            player.networkHandler.sendPacket(
                new WorldTimeUpdateS2CPacket(serverWorld.getTime(), serverWorld.getTimeOfDay(),
                    serverWorld.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE)));
          }
        }
      }
    }
  }

  @Override
  public void tickSessionOn() {
      if (!active) {
          return;
      }
    float sessionPassedTime = ((float) currentSession.passedTime - activatedAt);
      if (sessionPassedTime < 0) {
          return;
      }
      if (sessionPassedTime > 3600 && sessionPassedTime < 3700 && !isNerfed()) {
          OtherUtils.executeCommand("weather clear");
      }
    int currentDiv = (int) ((currentSession.passedTime) / updateRate);
    if (lastDiv != currentDiv) {
      lastDiv = currentDiv;

      float progress =
          ((float) currentSession.passedTime - activatedAt) / (currentSession.sessionLength
              - activatedAt);
      if (isNerfed()) {
        progress = ((float) currentSession.passedTime - activatedAt) / (20 * 60 * 5);
        if (progress >= 1 && !Callback.allWildcardsPhaseReached) {
          deactivate();
          WildcardManager.fadedWildcard();
          NetworkHandlerServer.sendUpdatePackets();
          return;
        }
      }
      progress = Math.clamp(progress, 0, 1);
            /*
            if (progress < 0.492f) {
                progress = 0.311774f * (float) Math.pow(progress, 0.7);
            }
            else {
                progress = (float) Math.pow(1.8*progress-0.87f, 3) + 0.19f;
            }
            */
      float rate;
      if (progress < 0.5f) {
        rate = getMinTickRate() + (NORMAL_TICK_RATE - getMinTickRate()) * (progress * 2);
      } else {
        rate = NORMAL_TICK_RATE + (getMaxTickRate() - NORMAL_TICK_RATE) * (progress * 2 - 1);
      }
      rate = Math.min(rate, getMaxTickRate());
      setWorldSpeed(rate);
    }
  }

  @Override
  public void deactivate() {
    super.deactivate();
    setWorldSpeed(NORMAL_TICK_RATE);
    lastDiv = -1;
    OtherUtils.executeCommand(
        "/execute as @e[type=minecraft:creeper] run data modify entity @s Fuse set value 30s");
  }

  @Override
  public void activate() {
      if (!isNerfed()) {
          TaskScheduler.scheduleTask(50, () -> OtherUtils.executeCommand("weather rain"));
      }
    TaskScheduler.scheduleTask(115, () -> {
      activatedAt = (int) currentSession.passedTime + 400;
      lastDiv = -1;
      PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(),
          SoundEvent.of(Identifier.ofVanilla("wildlife_time_slow_down")));
      slowlySetWorldSpeed(getMinTickRate(), 18);
        if (!isNerfed() && getMinTickRate() <= 4) {
            TaskScheduler.scheduleTask(18,
                () -> NetworkHandlerServer.sendLongPackets(PacketNames.TIME_DILATION,
                    System.currentTimeMillis()));
        }
      TaskScheduler.scheduleTask(19, super::activate);
    });
  }
}
