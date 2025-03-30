package net.mat0u5.lifeseries.series.wildlife;

import net.mat0u5.lifeseries.config.ConfigManager;
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
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
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
            int lives = seriesConfig.getOrCreateInt("default_lives", 6);
            setPlayerLives(player, lives);
        }
        WildcardManager.resetWildcardsOnPlayerJoin(player);
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
    }

    @Override
    public boolean isAllowedToAttack(ServerPlayerEntity attacker, ServerPlayerEntity victim) {
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
            addPlayerLife(killer);
            gaveLife = true;
        }
        if (isAllowedToAttack) return;
        OtherUtils.broadcastMessageToAdmins(Text.of("§c [Unjustified Kill?] §f"+victim.getNameForScoreboard() + "§7 was killed by §f"
                +killer.getNameForScoreboard() + "§7, who is not §cred name§7 (nor a §eyellow name§7, with the victim being a §2dark green name§7)"));
        if (gaveLife) OtherUtils.broadcastMessageToAdmins(Text.of("§7Remember to remove a life from the killer (using §f/lives remove <player>§7) if this was indeed an unjustified kill."));
    }


    @Override
    public void onClaimKill(ServerPlayerEntity killer, ServerPlayerEntity victim) {
        super.onClaimKill(killer, victim);
        if (isOnAtLeastLives(victim, 3, false)) {
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
        Hunger.SWITCH_DELAY = seriesConfig.getOrCreateInt("wildcard_hunger_randomize_interval", 36000);

        SizeShifting.MIN_SIZE = seriesConfig.getOrCreateDouble("wildcard_sizeshifting_min_size", 0.25);
        SizeShifting.MAX_SIZE = seriesConfig.getOrCreateDouble("wildcard_sizeshifting_max_size", 3);
        SizeShifting.SIZE_CHANGE_MULTIPLIER = seriesConfig.getOrCreateDouble("wildcard_sizeshifting_size_change_multiplier", 1);
        //SizeShifting.SAVE_FROM_FALLING = seriesConfig.getOrCreateBoolean("wildcard_sizeshifting_prevent_shift_falling", true);


        Snail.GLOBAL_SPEED_MULTIPLIER = seriesConfig.getOrCreateDouble("wildcard_snails_speed_multiplier", 1);
        Snail.SHOULD_DROWN_PLAYER = seriesConfig.getOrCreateBoolean("wildcard_snails_drown_players", true);

        TimeDilation.MIN_TICK_RATE = (float) (20.0 * seriesConfig.getOrCreateDouble("wildcard_timedilation_min_speed", 0.05));
        TimeDilation.MAX_TICK_RATE = (float) (20.0 * seriesConfig.getOrCreateDouble("wildcard_timedilation_max_speed", 5));
        TimeDilation.MIN_PLAYER_MSPT = (float) (50.0 / seriesConfig.getOrCreateDouble("wildcard_timedilation_player_max_speed", 2));

        MobSwap.MAX_DELAY = seriesConfig.getOrCreateInt("wildcard_mobswap_start_spawn_delay", 7200);
        MobSwap.MIN_DELAY = seriesConfig.getOrCreateInt("wildcard_mobswap_end_spawn_delay", 2400);
        MobSwap.SPAWN_MOBS = seriesConfig.getOrCreateInt("wildcard_mobswap_spawn_mobs", 250);
        MobSwap.BOSS_CHANCE_MULTIPLIER = seriesConfig.getOrCreateDouble("wildcard_mobswap_boss_chance_multiplier", 1);

        TriviaBot.CAN_START_RIDING = seriesConfig.getOrCreateBoolean("wildcard_trivia_bots_can_enter_boats", true);
        TriviaWildcard.TRIVIA_BOTS_PER_PLAYER = seriesConfig.getOrCreateInt("wildcard_trivia_bots_per_player", 5);
        TriviaBot.EASY_TIME = seriesConfig.getOrCreateInt("wildcard_trivia_seconds_easy", 180);
        TriviaBot.NORMAL_TIME = seriesConfig.getOrCreateInt("wildcard_trivia_seconds_normal", 240);
        TriviaBot.HARD_TIME = seriesConfig.getOrCreateInt("wildcard_trivia_seconds_hard", 300);
        WindCharge.MAX_MACE_DAMAGE = seriesConfig.getOrCreateInt("wildcard_superpowers_windcharge_max_mace_damage", 2);

        Snails.loadConfig();
        Snails.loadSnailNames();
        Snails.reloadSnailNames();
        Snails.reloadSnailSkins();
        TriviaWildcard.reload();
    }

    @Override
    public void modifyMobDrops(LivingEntity entity, DamageSource damageSource) {
        super.modifyMobDrops(entity, damageSource);
        if (damageSource.getSource() instanceof PlayerEntity) {
            if (entity instanceof WardenEntity || entity instanceof WitherEntity) {
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
        AttributeUtils.resetMaxPlayerHealth(player);

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
        if (source.getType() == player.getDamageSources().fall().getType()) {
            if (SuperpowersWildcard.hasActivePower(player, Superpowers.FLIGHT)) {
                if (SuperpowersWildcard.getSuperpowerInstance(player) instanceof Flight power) {
                    if (power.isLaunchedUp) {
                        power.isLaunchedUp = false;
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
