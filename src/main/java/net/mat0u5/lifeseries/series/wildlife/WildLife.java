package net.mat0u5.lifeseries.series.wildlife;

import net.mat0u5.lifeseries.resources.config.ConfigManager;
import net.mat0u5.lifeseries.entity.snail.Snail;
import net.mat0u5.lifeseries.entity.triviabot.TriviaBot;
import net.mat0u5.lifeseries.series.Series;
import net.mat0u5.lifeseries.series.SeriesList;
import net.mat0u5.lifeseries.series.wildlife.wildcards.WildcardManager;
import net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.*;
import net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.snails.Snails;
import net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.superpowers.SuperpowersWildcard;
import net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.superpowers.superpower.*;
import net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.trivia.TriviaWildcard;
import net.mat0u5.lifeseries.utils.*;
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
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

import static net.mat0u5.lifeseries.Main.seriesConfig;

public class WildLife extends Series {
    public static final String COMMANDS_ADMIN_TEXT = "/lifeseries, /session, /claimkill, /lives, /wildcard, /superpower, /snail";
    public static final String COMMANDS_TEXT = "/claimkill, /lives, /snail";

    @Override
    public SeriesList getSeries() {
        return SeriesList.WILD_LIFE;
    }

    @Override
    public ConfigManager getConfig() {
        return new WildLifeConfig();
    }

    @Override
    public void onPlayerJoin(ServerPlayerEntity player) {
        super.onPlayerJoin(player);

        if (!hasAssignedLives(player)) {
            int lives = seriesConfig.DEFAULT_LIVES.get(seriesConfig);
            setPlayerLives(player, lives);
        }
        WildcardManager.onPlayerJoin(player);
    }

    @Override
    public void onPlayerFinishJoining(ServerPlayerEntity player) {
        if (PermissionManager.isAdmin(player)) {
            player.sendMessage(Text.of("§7Wild Life commands: §r"+COMMANDS_ADMIN_TEXT));
        }
        else {
            player.sendMessage(Text.of("§7Wild Life non-admin commands: §r"+COMMANDS_TEXT));
        }
        super.onPlayerFinishJoining(player);
        WildcardManager.onPlayerFinishJoining(player);
    }

    @Override
    public boolean isAllowedToAttack(ServerPlayerEntity attacker, ServerPlayerEntity victim) {
        if (Necromancy.isRessurectedPlayer(victim) || Necromancy.isRessurectedPlayer(attacker)) return true;
        if (isOnLastLife(attacker, false)) return true;
        if (attacker.getPrimeAdversary() == victim && (isOnLastLife(victim, false))) return true;

        if (isOnSpecificLives(attacker, 2, false) && isOnAtLeastLives(victim, 3, false)) return true;
        return attacker.getPrimeAdversary() == victim && isOnSpecificLives(victim, 2, false) && isOnAtLeastLives(attacker, 3, false);
    }

    @Override
    public void onPlayerKilledByPlayer(ServerPlayerEntity victim, ServerPlayerEntity killer) {
        boolean gaveLife = false;
        boolean isAllowedToAttack = isAllowedToAttack(killer, victim);
        if (isOnAtLeastLives(victim, 4, false)) {
            if (Necromancy.isRessurectedPlayer(killer) && seriesConfig instanceof WildLifeConfig config) {
                if (WildLifeConfig.WILDCARD_SUPERPOWERS_ZOMBIES_REVIVE_BY_KILLING_DARK_GREEN.get(config)) {
                    Integer currentLives = getPlayerLives(killer);
                    if (currentLives == null) currentLives = 0;
                    int lives = currentLives + 1;
                    if (lives <= 0) {
                        ScoreboardUtils.setScore(ScoreHolder.fromName(killer.getNameForScoreboard()), "Lives", lives);
                    }
                    else {
                        addPlayerLife(killer);
                    }
                    gaveLife = true;
                }
            }
            else {
                addPlayerLife(killer);
                gaveLife = true;
            }
        }
        if (isAllowedToAttack) return;
        OtherUtils.broadcastMessageToAdmins(Text.of("§c [Unjustified Kill?] §f"+victim.getNameForScoreboard() + "§7 was killed by §f"
                +killer.getNameForScoreboard() + "§7, who is not §cred name§7 (nor a §eyellow name§7, with the victim being a §2dark green name§7)"));
        if (gaveLife) OtherUtils.broadcastMessageToAdmins(Text.of("§7Remember to remove a life from the killer (using §f/lives remove <player>§7) if this was indeed an unjustified kill."));
    }


    @Override
    public void onClaimKill(ServerPlayerEntity killer, ServerPlayerEntity victim) {
        super.onClaimKill(killer, victim);
        if (isOnAtLeastLives(victim, 4, false)) {
            addPlayerLife(killer);
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
        if (super.sessionStart()) {
            WildcardManager.onSessionStart();
            activeActions.addAll(
                    List.of(WildcardManager.wildcardNotice, WildcardManager.startWildcards)
            );
            return true;
        }
        return false;
    }

    @Override
    public void sessionEnd() {
        WildcardManager.onSessionEnd();
        super.sessionEnd();
    }

    @Override
    public void initialize() {
        super.initialize();
        Snails.loadConfig();
        Snails.loadSnailNames();
        TriviaBot.initializeItemSpawner();
    }

    @Override
    public void reload() {
        super.reload();
        if (!(seriesConfig instanceof WildLifeConfig config)) return;
        Hunger.SWITCH_DELAY = config.WILDCARD_HUNGER_RANDOMIZE_INTERVAL.get(config);

        SizeShifting.MIN_SIZE = config.WILDCARD_SIZESHIFTING_MIN_SIZE.get(config);
        SizeShifting.MAX_SIZE = config.WILDCARD_SIZESHIFTING_MAX_SIZE.get(config);
        SizeShifting.SIZE_CHANGE_MULTIPLIER = config.WILDCARD_SIZESHIFTING_SIZE_CHANGE_MULTIPLIER.get(config);
        //SizeShifting.SAVE_FROM_FALLING = config.WILDCARD_SIZESHIFTING_PREVENT_SHIFT_FALLING.get(config);


        Snail.GLOBAL_SPEED_MULTIPLIER = config.WILDCARD_SNAILS_SPEED_MULTIPLIER.get(config);
        Snail.SHOULD_DROWN_PLAYER = config.WILDCARD_SNAILS_DROWN_PLAYERS.get(config);

        TimeDilation.MIN_TICK_RATE = (float) (20.0 * config.WILDCARD_TIMEDILATION_MIN_SPEED.get(config));
        TimeDilation.MAX_TICK_RATE = (float) (20.0 * config.WILDCARD_TIMEDILATION_MAX_SPEED.get(config));
        TimeDilation.MIN_PLAYER_MSPT = (float) (50.0 / config.WILDCARD_TIMEDILATION_PLAYER_MAX_SPEED.get(config));

        MobSwap.MAX_DELAY = config.WILDCARD_MOBSWAP_START_SPAWN_DELAY.get(config);
        MobSwap.MIN_DELAY = config.WILDCARD_MOBSWAP_END_SPAWN_DELAY.get(config);
        MobSwap.SPAWN_MOBS = config.WILDCARD_MOBSWAP_SPAWN_MOBS.get(config);
        MobSwap.BOSS_CHANCE_MULTIPLIER = config.WILDCARD_MOBSWAP_BOSS_CHANCE_MULTIPLIER.get(config);

        TriviaBot.CAN_START_RIDING = config.WILDCARD_TRIVIA_BOTS_CAN_ENTER_BOATS.get(config);
        TriviaWildcard.TRIVIA_BOTS_PER_PLAYER = config.WILDCARD_TRIVIA_BOTS_PER_PLAYER.get(config);
        TriviaBot.EASY_TIME = config.WILDCARD_TRIVIA_SECONDS_EASY.get(config);
        TriviaBot.NORMAL_TIME = config.WILDCARD_TRIVIA_SECONDS_NORMAL.get(config);
        TriviaBot.HARD_TIME = config.WILDCARD_TRIVIA_SECONDS_HARD.get(config);
        WindCharge.MAX_MACE_DAMAGE = config.WILDCARD_SUPERPOWERS_WINDCHARGE_MAX_MACE_DAMAGE.get(config);

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
            if (entity instanceof WardenEntity || entity instanceof WitherEntity || entity instanceof EnderDragonEntity) {
                //? if <= 1.21 {
                entity.dropStack(Items.TOTEM_OF_UNDYING.getDefaultStack());
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
    }

    public static void changedPlayerTeam(ServerPlayerEntity player) {
        if (SuperpowersWildcard.hasActivePower(player, Superpowers.CREAKING)) {
            if (SuperpowersWildcard.getSuperpowerInstance(player) instanceof Creaking creakingPower) {
                creakingPower.deactivate();
            }
        }
    }

    @Override
    public void onPlayerDamage(ServerPlayerEntity player, DamageSource source, float amount, CallbackInfo ci) {
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
    public void onPrePlayerDamage(ServerPlayerEntity player, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (source.getType() == player.getDamageSources().fall().getType() ||
            source.getType() == player.getDamageSources().stalagmite().getType() ||
            source.getType() == player.getDamageSources().flyIntoWall().getType()) {
            if (SuperpowersWildcard.hasActivePower(player, Superpowers.FLIGHT)) {
                if (SuperpowersWildcard.getSuperpowerInstance(player) instanceof Flight power) {
                    if (power.isLaunchedUp) {
                        if (source.getType() != player.getDamageSources().flyIntoWall().getType()) power.isLaunchedUp = false;
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
            if (SuperpowersWildcard.hasActivatedPower(player, Superpowers.SUPER_PUNCH) && player.hasVehicle()) {
                if (player.getVehicle() instanceof ServerPlayerEntity) {
                    cir.setReturnValue(false);
                    return;
                }
            }
        }
    }

    @Override
    public void onRightClickEntity(ServerPlayerEntity player, World world, Hand hand, Entity entity, EntityHitResult hitResult) {
        if (SuperpowersWildcard.hasActivatedPower(player, Superpowers.SUPER_PUNCH)) {
            if (SuperpowersWildcard.getSuperpowerInstance(player) instanceof SuperPunch power) {
                power.tryRideEntity(entity);
            }
        }
    }

    @Override
    public void onAttackEntity(ServerPlayerEntity player, World world, Hand hand, Entity entity, EntityHitResult hitResult) {
        if (SuperpowersWildcard.hasActivatedPower(player, Superpowers.INVISIBILITY)) {
            if (SuperpowersWildcard.getSuperpowerInstance(player) instanceof Invisibility power) {
                power.onAttack();
            }
        }
    }
}
