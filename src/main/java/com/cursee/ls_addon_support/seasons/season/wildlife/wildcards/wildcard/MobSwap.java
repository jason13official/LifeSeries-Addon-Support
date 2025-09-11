package com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard;

import static com.cursee.ls_addon_support.LSAddonSupport.currentSession;
import static com.cursee.ls_addon_support.LSAddonSupport.server;

import com.cursee.ls_addon_support.entity.pathfinder.PathFinder;
import com.cursee.ls_addon_support.entity.snail.Snail;
import com.cursee.ls_addon_support.entity.triviabot.TriviaBot;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.Wildcard;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.WildcardManager;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.Wildcards;
import com.cursee.ls_addon_support.seasons.session.SessionTranscript;
import com.cursee.ls_addon_support.utils.other.TaskScheduler;
import com.cursee.ls_addon_support.utils.player.PlayerUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class MobSwap extends Wildcard {

  public static final List<Integer> eggSounds = List.of(0, 20, 35, 48, 59, 70, 80, 89, 97, 104, 110,
      115, 119, 122, 124, 126, 128, 129, 130, 131, 132, 133, 134, 135, 136, 137, 138, 139, 140);
  public static final Map<EntityType<?>, Integer> entityEntries = new HashMap<>();
  private static final Random rnd = new Random();
  public static int activatedAt = -1;
  public static int lastDiv0 = 0;
  public static int lastDiv = 0;
  public static int mobsLeftDiv = 0;
  public static int swaps = -1;
  public static double BOSS_CHANCE_MULTIPLIER = 1;
  public static int MIN_DELAY = 2400;
  public static int MAX_DELAY = 7200;
  public static int SPAWN_MOBS = 250;
  public static int mobcapMonster = -1;
  public static int mobcapAnimal = -1;
  public static double bossChance = 0;
  public static boolean fastAnimalSpawn = false;

  public static void initializeEntityEntries() {
    entityEntries.put(EntityType.ALLAY, 1);
    entityEntries.put(EntityType.BAT, 2);
    entityEntries.put(EntityType.CAT, 3);
    entityEntries.put(EntityType.CHICKEN, 4);
    entityEntries.put(EntityType.RABBIT, 5);
    entityEntries.put(EntityType.SQUID, 6);
    entityEntries.put(EntityType.TROPICAL_FISH, 7);
    entityEntries.put(EntityType.TURTLE, 8);
    entityEntries.put(EntityType.COD, 9);
    entityEntries.put(EntityType.COW, 10);

    entityEntries.put(EntityType.DONKEY, 11);
    entityEntries.put(EntityType.GLOW_SQUID, 12);
    entityEntries.put(EntityType.MOOSHROOM, 13);
    entityEntries.put(EntityType.MULE, 14);
    entityEntries.put(EntityType.PIG, 15);
    entityEntries.put(EntityType.SHEEP, 16);
    entityEntries.put(EntityType.SNIFFER, 17);
    entityEntries.put(EntityType.WANDERING_TRADER, 19);

    entityEntries.put(EntityType.FROG, 20);
    entityEntries.put(EntityType.CAMEL, 20);
    entityEntries.put(EntityType.HORSE, 22);
    entityEntries.put(EntityType.OCELOT, 24);
    entityEntries.put(EntityType.PARROT, 26);
    entityEntries.put(EntityType.AXOLOTL, 28);
    entityEntries.put(EntityType.FOX, 30);
    entityEntries.put(EntityType.GOAT, 32);
    entityEntries.put(EntityType.PANDA, 34);
    entityEntries.put(EntityType.LLAMA, 36);
    entityEntries.put(EntityType.DOLPHIN, 38);
    entityEntries.put(EntityType.BEE, 40);
    entityEntries.put(EntityType.WOLF, 42);
    entityEntries.put(EntityType.TRADER_LLAMA, 44);

    entityEntries.put(EntityType.POLAR_BEAR, 45);
    entityEntries.put(EntityType.PIGLIN, 48);
    entityEntries.put(EntityType.ZOMBIFIED_PIGLIN, 50);
    entityEntries.put(EntityType.SILVERFISH, 52);
    entityEntries.put(EntityType.SLIME, 54);
    entityEntries.put(EntityType.SPIDER, 56);
    entityEntries.put(EntityType.ENDERMAN, 58);
    entityEntries.put(EntityType.PHANTOM, 60);
    entityEntries.put(EntityType.PILLAGER, 62);

    entityEntries.put(EntityType.CAVE_SPIDER, 64);
    entityEntries.put(EntityType.DROWNED, 66);
    entityEntries.put(EntityType.HOGLIN, 68);
    entityEntries.put(EntityType.HUSK, 70);
    entityEntries.put(EntityType.SKELETON, 72);
    entityEntries.put(EntityType.STRAY, 74);
    entityEntries.put(EntityType.ZOMBIE, 76);
    entityEntries.put(EntityType.ZOMBIE_VILLAGER, 78);

    entityEntries.put(EntityType.CREEPER, 80);
    entityEntries.put(EntityType.GUARDIAN, 82);
    entityEntries.put(EntityType.WITCH, 84);
    entityEntries.put(EntityType.EVOKER, 86);
    entityEntries.put(EntityType.BLAZE, 88);
    entityEntries.put(EntityType.ENDERMITE, 90);
    entityEntries.put(EntityType.GHAST, 92);
    entityEntries.put(EntityType.MAGMA_CUBE, 94);
    entityEntries.put(EntityType.VEX, 96);
    entityEntries.put(EntityType.WITHER_SKELETON, 98);
    entityEntries.put(EntityType.ILLUSIONER, 100);
    entityEntries.put(EntityType.PIGLIN_BRUTE, 102);
    entityEntries.put(EntityType.SHULKER, 104);
    entityEntries.put(EntityType.VINDICATOR, 106);
    entityEntries.put(EntityType.ZOGLIN, 108);
    entityEntries.put(EntityType.RAVAGER, 110);
  }

  private static void killNonNamedMobs() {
      if (server == null) {
          return;
      }
    for (ServerWorld world : server.getWorlds()) {
      List<Entity> toKill = new ArrayList<>();
      world.iterateEntities().forEach(entity -> {
          if (!(entity instanceof LivingEntity)) {
              return;
          }
          if (entity instanceof PlayerEntity) {
              return;
          }
          if (entity instanceof Snail) {
              return;
          }
          if (entity instanceof TriviaBot) {
              return;
          }
          if (entity instanceof PathFinder) {
              return;
          }
          if (entity.hasCustomName()) {
              return;
          }
        toKill.add(entity);
      });

      boolean mobLoot = world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT);
        if (mobLoot) {
            world.getGameRules().get(GameRules.DO_MOB_LOOT).set(false, world.getServer());
        }
      for (Entity entity : toKill) {
          entity.kill((ServerWorld) entity.getWorld());
      }
        if (mobLoot) {
            world.getGameRules().get(GameRules.DO_MOB_LOOT).set(true, world.getServer());
        }
    }
  }

  private static void transformNonNamedMobs(float progress) {
    int dangerThresholdMin = Math.min(50, (int) (progress * 70));
    int dangerThresholdMax = Math.max(80, Math.min(115, (int) (progress * 50) + 80));

      if (server == null) {
          return;
      }
    for (ServerWorld world : server.getWorlds()) {
      List<Entity> toKill = new ArrayList<>();
      world.iterateEntities().forEach(entity -> {
          if (!(entity instanceof LivingEntity)) {
              return;
          }
          if (entity instanceof PlayerEntity) {
              return;
          }
          if (entity instanceof Snail) {
              return;
          }
          if (entity instanceof TriviaBot) {
              return;
          }
          if (entity instanceof PathFinder) {
              return;
          }
          if (entity.hasCustomName()) {
              return;
          }

        EntityType<?> randomMob = getRandomMob(progress, dangerThresholdMin, dangerThresholdMax);
        if (randomMob != null) {
          Entity newMob = randomMob.spawn(world, entity.getBlockPos(), SpawnReason.COMMAND);
          if (newMob != null) {
            newMob.refreshPositionAndAngles(entity.getX(), entity.getY(), entity.getZ(),
                entity.getYaw(), entity.getPitch());
            newMob.addCommandTag("mobswap");
            if (newMob instanceof MobEntity mobEntity) {
              mobEntity.setPersistent();
            }
            if (newMob instanceof WardenEntity wardenEntity) {
              wardenEntity.getBrain()
                  .remember(MemoryModuleType.DIG_COOLDOWN, Unit.INSTANCE, 12000000L);
            }
          }
        }
        toKill.add(entity);
      });

      toKill.forEach(Entity::discard);
    }
  }

  private static EntityType<?> getRandomMob(float progress, int dangerThresholdMin,
      int dangerThresholdMax) {
    List<EntityType<?>> possibleMobs = new ArrayList<>();

    for (Map.Entry<EntityType<?>, Integer> entry : entityEntries.entrySet()) {
      int dangerValue = entry.getValue();
      if (dangerValue >= dangerThresholdMin && dangerValue <= dangerThresholdMax) {
        possibleMobs.add(entry.getKey());
      }
    }

    if (progress > 0.7) {
      if (Math.random() < bossChance) {
        double random = Math.random();
        if (random < 0.33) {
          return EntityType.WARDEN;
        } else if (random < 0.66) {
          return EntityType.WITHER;
        } else {
          return EntityType.ELDER_GUARDIAN;
        }
      }
    }

    return possibleMobs.get(rnd.nextInt(possibleMobs.size()));
  }

  public static void killMobSwapMobs() {
      if (server == null) {
          return;
      }
    for (ServerWorld world : server.getWorlds()) {
      List<Entity> toKill = new ArrayList<>();
      world.iterateEntities().forEach(entity -> {
          if (!(entity instanceof LivingEntity)) {
              return;
          }
          if (entity instanceof PlayerEntity) {
              return;
          }
          if (entity instanceof Snail) {
              return;
          }
          if (entity instanceof TriviaBot) {
              return;
          }
          if (entity instanceof PathFinder) {
              return;
          }
          if (entity.hasCustomName()) {
              return;
          }
          if (!entity.getCommandTags().contains("mobswap")) {
              return;
          }
        toKill.add(entity);
      });

      boolean mobLoot = world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT);
        if (mobLoot) {
            world.getGameRules().get(GameRules.DO_MOB_LOOT).set(false, world.getServer());
        }
      for (Entity entity : toKill) {
          entity.kill((ServerWorld) entity.getWorld());
      }
        if (mobLoot) {
            world.getGameRules().get(GameRules.DO_MOB_LOOT).set(true, world.getServer());
        }
    }
  }

  public static void getSpawnCapacity(SpawnGroup group, CallbackInfoReturnable<Integer> cir) {
    if (group.getName().equalsIgnoreCase("monster") && mobcapMonster >= 0) {
      cir.setReturnValue(mobcapMonster);
    } else if (group.getName().equalsIgnoreCase("creature") && mobcapAnimal >= 0) {
      cir.setReturnValue(mobcapAnimal);
    }
  }

  public static void isRare(SpawnGroup group, CallbackInfoReturnable<Boolean> cir) {
    if (group.getName().equalsIgnoreCase("creature") && fastAnimalSpawn) {
      cir.setReturnValue(false);
    }
  }

  public static void isAcceptableSpawnPosition(ServerWorld world, Chunk chunk, BlockPos.Mutable pos,
      double squaredDistance, CallbackInfoReturnable<Boolean> cir) {
      if (!fastAnimalSpawn) {
          return;
      }
    if (squaredDistance < 4) {
      cir.setReturnValue(false);
    }
      ChunkPos chunkPos = new ChunkPos(pos);
      cir.setReturnValue(Objects.equals(chunkPos, chunk.getPos()) || world.canSpawnEntitiesAt(chunkPos));
  }

  @Override
  public String getId() {
    return Wildcards.MOB_SWAP;
  }

  @Override
  public void tickSessionOn() {
      if (server == null) {
          return;
      }

    int currentDiv0 = (int) (((float) currentSession.passedTime - activatedAt) / 40.0);
    if (lastDiv0 != currentDiv0) {
      int currentDiv = getDiv();
      if (lastDiv != currentDiv) {
        mobSwap();
        lastDiv = currentDiv;
      }
      lastDiv0 = currentDiv0;
    }
  }

  @Override
  public void activate() {
    activatedAt = (int) currentSession.passedTime;
    lastDiv = 0;
    mobsLeftDiv = 0;
    bossChance = 0;
    swaps = -1;
    initializeEntityEntries();
    super.activate();
  }

  @Override
  public void deactivate() {
    killMobSwapMobs();
    super.deactivate();
  }

  public int getDiv() {
    List<Integer> triggerTimes = new ArrayList<>();
    int lastTime = 0;
    if (MAX_DELAY > 2400) {
      lastTime = 2400;
        if (2400 > (currentSession.passedTime - activatedAt)) {
            triggerTimes.add(2400);
        }
    }
    while (lastTime < currentSession.sessionLength) {
      float sessionProgress = ((float) lastTime) / (currentSession.sessionLength);
      sessionProgress = Math.clamp(sessionProgress, 0, 1);
      lastTime += (int) (MAX_DELAY - sessionProgress * (MAX_DELAY - MIN_DELAY));
      if (lastTime > (currentSession.passedTime - activatedAt) && lastTime < (
          currentSession.sessionLength - MIN_DELAY)) {
        triggerTimes.add(lastTime);
      }
    }
    return triggerTimes.size();
  }

  public void mobSwap() {
    //TODO refactor
    List<ServerPlayerEntity> players = PlayerUtils.getAllPlayers();
    swaps++;
      if (swaps < 1) {
          return;
      }

    SessionTranscript.mobSwap();

    int spawnMobs;
    if (WildcardManager.isActiveWildcard(Wildcards.CALLBACK)) {
      spawnMobs = (int) (SPAWN_MOBS / 1.5);
    } else {
      spawnMobs = SPAWN_MOBS;
    }

    float progress =
        ((float) currentSession.passedTime - activatedAt) / (currentSession.sessionLength
            - activatedAt);
    if (progress > 0.7) {
      if (mobsLeftDiv == 0) {
        mobsLeftDiv = lastDiv;
      }
      if (spawnMobs != 0) {
        int totalMobsLeft = mobsLeftDiv * spawnMobs;
        bossChance = (2.0 / totalMobsLeft) * BOSS_CHANCE_MULTIPLIER;
      }
    }

    if (swaps > 1) {
      PlayerUtils.playSoundToPlayers(players, SoundEvents.BLOCK_BEACON_DEACTIVATE);
    }

    killNonNamedMobs();
    mobcapAnimal = 0;

    TaskScheduler.scheduleTask(120, () -> {
      mobcapAnimal = spawnMobs;
      fastAnimalSpawn = true;
    });

    int timeForSpawning = Math.max(120, (int) (spawnMobs / 2.5));

    for (int i = timeForSpawning; i > 120; i -= 20) {
      TaskScheduler.scheduleTask(i, () -> {
        PlayerUtils.playSoundToPlayers(players, SoundEvents.ENTITY_CHICKEN_EGG);
      });
    }

    for (int delay : eggSounds) {
      TaskScheduler.scheduleTask(timeForSpawning + delay, () -> {
        PlayerUtils.playSoundToPlayers(players, SoundEvents.ENTITY_CHICKEN_EGG);
      });
    }

    TaskScheduler.scheduleTask(timeForSpawning + 140, () -> {
      mobcapAnimal = -1;
      fastAnimalSpawn = false;
      WildcardManager.showDots();
    });

    TaskScheduler.scheduleTask(timeForSpawning + 240, () -> {
      PlayerUtils.playSoundToPlayers(players, SoundEvents.ENTITY_ELDER_GUARDIAN_CURSE, 0.2f, 1);
      PlayerUtils.playSoundToPlayers(players, SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, 0.2f, 1);
      transformNonNamedMobs(progress);
    });
  }
}
