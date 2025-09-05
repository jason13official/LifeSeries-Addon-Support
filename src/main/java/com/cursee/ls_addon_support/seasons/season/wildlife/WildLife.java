package com.cursee.ls_addon_support.seasons.season.wildlife;

import static com.cursee.ls_addon_support.LSAddonSupport.currentSession;
import static com.cursee.ls_addon_support.LSAddonSupport.seasonConfig;

import com.cursee.ls_addon_support.config.ConfigManager;
import com.cursee.ls_addon_support.entity.snail.Snail;
import com.cursee.ls_addon_support.entity.triviabot.TriviaBot;
import com.cursee.ls_addon_support.seasons.other.LivesManager;
import com.cursee.ls_addon_support.seasons.season.Season;
import com.cursee.ls_addon_support.seasons.season.Seasons;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.WildcardManager;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.Callback;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.Hunger;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.MobSwap;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.SizeShifting;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.TimeDilation;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.snails.Snails;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpower;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.SuperpowersWildcard;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower.AnimalDisguise;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower.Creaking;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower.Flight;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower.Invisibility;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower.Necromancy;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower.PlayerDisguise;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower.SuperPunch;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower.Superspeed;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower.TripleJump;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower.WindCharge;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.trivia.TriviaWildcard;
import com.cursee.ls_addon_support.utils.other.TextUtils;
import com.cursee.ls_addon_support.utils.player.AttributeUtils;
import com.cursee.ls_addon_support.utils.player.PlayerUtils;
import com.cursee.ls_addon_support.utils.player.ScoreboardUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.scoreboard.ScoreHolder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class WildLife extends Season {

  public static final String COMMANDS_ADMIN_TEXT = "/lifeseries, /session, /claimkill, /lives, /wildcard, /superpower, /snail";
  public static final String COMMANDS_TEXT = "/claimkill, /lives, /snail";
  public static boolean KILLING_DARK_GREENS_GAINS_LIVES = true;
  public static boolean BROADCAST_LIFE_GAIN = true;

  public static void changedPlayerTeam(ServerPlayerEntity player) {
    if (SuperpowersWildcard.hasActivePower(player, Superpowers.CREAKING)) {
      if (SuperpowersWildcard.getSuperpowerInstance(player) instanceof Creaking creakingPower) {
        creakingPower.deactivate();
      }
    }
  }

  @Override
  public Seasons getSeason() {
    return Seasons.WILD_LIFE;
  }

  @Override
  public ConfigManager createConfig() {
    Snails.loadConfig();
    return new WildLifeConfig();
  }

  @Override
  public String getAdminCommands() {
    return COMMANDS_ADMIN_TEXT;
  }

  @Override
  public String getNonAdminCommands() {
    return COMMANDS_TEXT;
  }

  @Override
  public void initialize() {
    super.initialize();
    Snails.loadConfig();
    Snails.loadSnailNames();
    TriviaBot.initializeItemSpawner();
  }

  @Override
  public void onPlayerJoin(ServerPlayerEntity player) {
    super.onPlayerJoin(player);
    WildcardManager.onPlayerJoin(player);
  }

  @Override
  public void onPlayerFinishJoining(ServerPlayerEntity player) {
    super.onPlayerFinishJoining(player);
    WildcardManager.onPlayerFinishJoining(player);
  }

  @Override
  public boolean isAllowedToAttack(ServerPlayerEntity attacker, ServerPlayerEntity victim,
      boolean allowSelfDefense) {
    if (Necromancy.isRessurectedPlayer(victim) || Necromancy.isRessurectedPlayer(attacker)) {
      return true;
    }
    if (livesManager.isOnSpecificLives(attacker, 2, false) && livesManager.isOnAtLeastLives(victim,
        3, false)) {
      return true;
    }
    return super.isAllowedToAttack(attacker, victim, allowSelfDefense);
  }

  @Override
  public void onPlayerKilledByPlayer(ServerPlayerEntity victim, ServerPlayerEntity killer) {
    boolean wasAllowedToAttack = isAllowedToAttack(killer, victim, false);
    boolean wasBoogeyCure = boogeymanManager.isBoogeymanThatCanBeCured(killer, victim);
    super.onPlayerKilledByPlayer(victim, killer);
    if (livesManager.isOnAtLeastLives(victim, 4, false) && wasAllowedToAttack && !wasBoogeyCure) {
      if (Necromancy.isRessurectedPlayer(killer) && seasonConfig instanceof WildLifeConfig config) {
        if (WildLifeConfig.WILDCARD_SUPERPOWERS_ZOMBIES_REVIVE_BY_KILLING_DARK_GREEN.get(config)) {
          Integer currentLives = livesManager.getPlayerLives(killer);
          if (currentLives == null) {
            currentLives = 0;
          }
          int lives = currentLives + 1;
          if (lives <= 0) {
            ScoreboardUtils.setScore(ScoreHolder.fromName(killer.getNameForScoreboard()),
                LivesManager.SCOREBOARD_NAME, lives);
          } else {
            broadcastLifeGain(killer);
            livesManager.addPlayerLife(killer);
          }
        }
      } else {
        if (KILLING_DARK_GREENS_GAINS_LIVES) {
          broadcastLifeGain(killer);
          livesManager.addPlayerLife(killer);
        }
      }
    }
  }

  @Override
  public void onClaimKill(ServerPlayerEntity killer, ServerPlayerEntity victim) {
    super.onClaimKill(killer, victim);
    if (livesManager.isOnAtLeastLives(victim, 4, false) && KILLING_DARK_GREENS_GAINS_LIVES) {
      broadcastLifeGain(killer);
      livesManager.addPlayerLife(killer);
    }
  }

  public void broadcastLifeGain(ServerPlayerEntity player) {
    if (BROADCAST_LIFE_GAIN) {
      PlayerUtils.broadcastMessage(
          TextUtils.format("{}§7 gained a life for killing a §2dark green§7 player.", player));
    }
  }

  @Override
  public void tickSessionOn(MinecraftServer server) {
    super.tickSessionOn(server);
    WildcardManager.tickSessionOn();
  }

  @Override
  public void tick(MinecraftServer server) {
    super.tick(server);
    WildcardManager.tick();
  }

  @Override
  public boolean sessionStart() {
    super.sessionStart();
    WildcardManager.onSessionStart();
    currentSession.activeActions.addAll(
        WildcardManager.getActions()
    );
    return true;
  }

  @Override
  public void sessionEnd() {
    WildcardManager.onSessionEnd();
    super.sessionEnd();
  }

  @Override
  public void reload() {
    super.reload();
    if (!(seasonConfig instanceof WildLifeConfig config)) {
      return;
    }
    Hunger.SWITCH_DELAY = 20 * WildLifeConfig.WILDCARD_HUNGER_RANDOMIZE_INTERVAL.get(config);
    Hunger.HUNGER_EFFECT_LEVEL = WildLifeConfig.WILDCARD_HUNGER_EFFECT_LEVEL.get(config);
    Hunger.NUTRITION_CHANCE = WildLifeConfig.WILDCARD_HUNGER_NUTRITION_CHANCE.get(config);
    Hunger.SATURATION_CHANCE = WildLifeConfig.WILDCARD_HUNGER_SATURATION_CHANCE.get(config);
    Hunger.EFFECT_CHANCE = WildLifeConfig.WILDCARD_HUNGER_EFFECT_CHANCE.get(config);
    Hunger.AVG_EFFECT_DURATION = WildLifeConfig.WILDCARD_HUNGER_AVG_EFFECT_DURATION.get(config);

    SizeShifting.MIN_SIZE = WildLifeConfig.WILDCARD_SIZESHIFTING_MIN_SIZE.get(config);
    SizeShifting.MAX_SIZE = WildLifeConfig.WILDCARD_SIZESHIFTING_MAX_SIZE.get(config);
    SizeShifting.SIZE_CHANGE_MULTIPLIER = WildLifeConfig.WILDCARD_SIZESHIFTING_SIZE_CHANGE_MULTIPLIER.get(
        config);
    //SizeShifting.SAVE_FROM_FALLING = config.WILDCARD_SIZESHIFTING_PREVENT_SHIFT_FALLING.get(config);

    Snail.GLOBAL_SPEED_MULTIPLIER = WildLifeConfig.WILDCARD_SNAILS_SPEED_MULTIPLIER.get(config);
    Snail.SHOULD_DROWN_PLAYER = WildLifeConfig.WILDCARD_SNAILS_DROWN_PLAYERS.get(config);

    TimeDilation.MIN_TICK_RATE = (float) (20.0 * WildLifeConfig.WILDCARD_TIMEDILATION_MIN_SPEED.get(
        config));
    TimeDilation.MAX_TICK_RATE = (float) (20.0 * WildLifeConfig.WILDCARD_TIMEDILATION_MAX_SPEED.get(
        config));
    TimeDilation.MIN_PLAYER_MSPT = (float) (50.0
        / WildLifeConfig.WILDCARD_TIMEDILATION_PLAYER_MAX_SPEED.get(config));

    MobSwap.MAX_DELAY = 20 * WildLifeConfig.WILDCARD_MOBSWAP_START_SPAWN_DELAY.get(config);
    MobSwap.MIN_DELAY = 20 * WildLifeConfig.WILDCARD_MOBSWAP_END_SPAWN_DELAY.get(config);
    MobSwap.SPAWN_MOBS = WildLifeConfig.WILDCARD_MOBSWAP_SPAWN_MOBS.get(config);
    MobSwap.BOSS_CHANCE_MULTIPLIER = WildLifeConfig.WILDCARD_MOBSWAP_BOSS_CHANCE_MULTIPLIER.get(
        config);

    TriviaBot.CAN_START_RIDING = WildLifeConfig.WILDCARD_TRIVIA_BOTS_CAN_ENTER_BOATS.get(config);
    TriviaWildcard.TRIVIA_BOTS_PER_PLAYER = WildLifeConfig.WILDCARD_TRIVIA_BOTS_PER_PLAYER.get(
        config);
    TriviaBot.EASY_TIME = WildLifeConfig.WILDCARD_TRIVIA_SECONDS_EASY.get(config);
    TriviaBot.NORMAL_TIME = WildLifeConfig.WILDCARD_TRIVIA_SECONDS_NORMAL.get(config);
    TriviaBot.HARD_TIME = WildLifeConfig.WILDCARD_TRIVIA_SECONDS_HARD.get(config);
    WindCharge.MAX_MACE_DAMAGE = WildLifeConfig.WILDCARD_SUPERPOWERS_WINDCHARGE_MAX_MACE_DAMAGE.get(
        config);
    Superspeed.STEP_UP = WildLifeConfig.WILDCARD_SUPERPOWERS_SUPERSPEED_STEP.get(config);
    WildcardManager.ACTIVATE_WILDCARD_MINUTE = WildLifeConfig.ACTIVATE_WILDCARD_MINUTE.get(config);
    KILLING_DARK_GREENS_GAINS_LIVES = WildLifeConfig.KILLING_DARK_GREENS_GAINS_LIVES.get(config);
    BROADCAST_LIFE_GAIN = WildLifeConfig.BROADCAST_LIFE_GAIN.get(config);
    SuperpowersWildcard.WILDCARD_SUPERPOWERS_DISABLE_INTRO_THEME = WildLifeConfig.WILDCARD_SUPERPOWERS_DISABLE_INTRO_THEME.get(
        config);
    SuperpowersWildcard.setBlacklist(
        WildLifeConfig.WILDCARD_SUPERPOWERS_POWER_BLACKLIST.get(config));
    Callback.setBlacklist(WildLifeConfig.WILDCARD_CALLBACK_WILDCARDS_BLACKLIST.get(config));
    Callback.TURN_OFF = WildLifeConfig.WILDCARD_CALLBACK_TURN_OFF.get(config);

    Snails.loadConfig();
    Snails.loadSnailNames();
    Snails.reloadSnailNames();
    Snails.reloadSnailSkins();
    TriviaWildcard.reload();
  }

  @Override
  public void modifyEntityDrops(LivingEntity entity, DamageSource damageSource) {
    super.modifyEntityDrops(entity, damageSource);
    if (damageSource.getSource() instanceof PlayerEntity) {
      if (entity instanceof WardenEntity || entity instanceof WitherEntity
          || entity instanceof EnderDragonEntity) {
        //? if <= 1.21 {
        entity.dropStack((ServerWorld) entity.getWorld(), Items.TOTEM_OF_UNDYING.getDefaultStack());
        //?} else {
        /*entity.dropStack((ServerWorld) entity.getWorld(), Items.TOTEM_OF_UNDYING.getDefaultStack());
         *///?}
      }
    }
  }

  @Override
  public void onPlayerDeath(ServerPlayerEntity player, DamageSource source) {
    super.onPlayerDeath(player, source);

    TriviaBot.cursedGigantificationPlayers.remove(player.getUuid());
    TriviaBot.cursedHeartPlayers.remove(player.getUuid());
    AttributeUtils.resetMaxPlayerHealthIfNecessary(player);

    TriviaBot.cursedMoonJumpPlayers.remove(player.getUuid());
    AttributeUtils.resetPlayerJumpHeight(player);

    Superpower power = SuperpowersWildcard.getSuperpowerInstance(player);
    if (power != null) {
      power.deactivate();
    }
  }

  @Override
  public void onPlayerDisconnect(ServerPlayerEntity player) {
    super.onPlayerDisconnect(player);

    Superpower power = SuperpowersWildcard.getSuperpowerInstance(player);
    if (power != null) {
      power.deactivate();
    }
  }

  @Override
  public void onPlayerDamage(ServerPlayerEntity player, DamageSource source, float amount,
      CallbackInfo ci) {
    if (SuperpowersWildcard.hasActivatedPower(player, Superpowers.PLAYER_DISGUISE)) {
      if (SuperpowersWildcard.getSuperpowerInstance(player) instanceof PlayerDisguise power) {
        power.onTakeDamage();
      }
    }
    if (SuperpowersWildcard.hasActivatedPower(player, Superpowers.ANIMAL_DISGUISE)) {
      if (SuperpowersWildcard.getSuperpowerInstance(player) instanceof AnimalDisguise power) {
        power.onTakeDamage();
      }
    }
    if (SuperpowersWildcard.hasActivatedPower(player, Superpowers.INVISIBILITY)) {
      if (SuperpowersWildcard.getSuperpowerInstance(player) instanceof Invisibility power) {
        power.onTakeDamage();
      }
    }
  }

  @Override
  public void onPrePlayerDamage(ServerPlayerEntity player, DamageSource source, float amount,
      CallbackInfoReturnable<Boolean> cir) {
    if (source.getType() == player.getDamageSources().fall().getType() ||
        source.getType() == player.getDamageSources().stalagmite().getType() ||
        source.getType() == player.getDamageSources().flyIntoWall().getType()) {
      if (SuperpowersWildcard.hasActivePower(player, Superpowers.FLIGHT)) {
        if (SuperpowersWildcard.getSuperpowerInstance(player) instanceof Flight power) {
          if (power.isLaunchedUp) {
            if (source.getType() != player.getDamageSources().flyIntoWall().getType()) {
              power.isLaunchedUp = false;
            }
            cir.setReturnValue(false);
            return;
          }
        }
      }
      if (SuperpowersWildcard.hasActivatedPower(player, Superpowers.TRIPLE_JUMP)) {
        if (SuperpowersWildcard.getSuperpowerInstance(player) instanceof TripleJump power) {
          if (power.isInAir) {
            power.isInAir = false;
            cir.setReturnValue(false);
            return;
          }
        }
      }
      if (SuperpowersWildcard.hasActivatedPower(player, Superpowers.SUPER_PUNCH)
          && player.hasVehicle()) {
        if (player.getVehicle() instanceof ServerPlayerEntity) {
          cir.setReturnValue(false);
        }
      }
    }
  }

  @Override
  public void onRightClickEntity(ServerPlayerEntity player, World world, Hand hand, Entity entity,
      EntityHitResult hitResult) {
    if (SuperpowersWildcard.hasActivatedPower(player, Superpowers.SUPER_PUNCH)) {
      if (SuperpowersWildcard.getSuperpowerInstance(player) instanceof SuperPunch power) {
        power.tryRideEntity(entity);
      }
    }
  }

  @Override
  public void onAttackEntity(ServerPlayerEntity player, World world, Hand hand, Entity entity,
      EntityHitResult hitResult) {
    if (SuperpowersWildcard.hasActivatedPower(player, Superpowers.INVISIBILITY)) {
      if (SuperpowersWildcard.getSuperpowerInstance(player) instanceof Invisibility power) {
        power.onAttack();
      }
    }
  }

  @Override
  public void onUpdatedInventory(ServerPlayerEntity player) {
    super.onUpdatedInventory(player);
    Hunger.updateInventory(player);
  }
}
