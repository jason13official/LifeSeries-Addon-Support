package net.mat0u5.lifeseries.series;

import net.mat0u5.lifeseries.events.Events;
import net.mat0u5.lifeseries.resources.config.ConfigManager;
import net.mat0u5.lifeseries.entity.snail.Snail;
import net.mat0u5.lifeseries.entity.triviabot.TriviaBot;
import net.mat0u5.lifeseries.series.wildlife.WildLife;
import net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.superpowers.superpower.Necromancy;
import net.mat0u5.lifeseries.utils.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.ElderGuardianEntity;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.scoreboard.ScoreHolder;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

import static net.mat0u5.lifeseries.Main.*;

public abstract class Series extends Session {
    public static final String RESOURCEPACK_MAIN_URL = "https://github.com/Mat0u5/LifeSeries-Resources/releases/download/release-main-af45fc947c22c9ee91ec021d998318a5f2d5bdaf/RP.zip";
    public static final String RESOURCEPACK_MAIN_SHA ="38a74dc7c112e1e9c009e71f544b1b050a01560e";
    public boolean NO_HEALING = false;
    public boolean SHOW_DEATH_TITLE = false;
    public int GIVELIFE_MAX_LIVES = 99;
    public boolean TAB_LIST_SHOW_DEAD_PLAYERS = true;
    public boolean TAB_LIST_SHOW_LIVES = false;

    public abstract SeriesList getSeries();
    public abstract ConfigManager getConfig();

    public Blacklist createBlacklist() {
        return new Blacklist();
    }

    public void initialize() {
        reload();
    }

    public void updateStuff() {
        if (server == null) return;
        if (server.getOverworld().getWorldBorder().getSize() > 1000000 && seriesConfig.AUTO_SET_WORLDBORDER.get(seriesConfig)) {
            OtherUtils.executeCommand("worldborder set 500");
        }

        server.getGameRules().get(GameRules.KEEP_INVENTORY).set(seriesConfig.KEEP_INVENTORY.get(seriesConfig), server);
        server.getGameRules().get(GameRules.NATURAL_REGENERATION).set(!NO_HEALING, server);
        //? if >= 1.21.6 {
        /*server.getGameRules().get(GameRules.LOCATOR_BAR).set(seriesConfig.LOCATOR_BAR.get(seriesConfig), server);
        *///?}

        ScoreboardObjective currentListObjective = ScoreboardUtils.getObjectiveInSlot(ScoreboardDisplaySlot.LIST);
        if (TAB_LIST_SHOW_LIVES) {
            ScoreboardUtils.setObjectiveInSlot(ScoreboardDisplaySlot.LIST, "Lives");
        }
        else if (currentListObjective != null) {
            if (currentListObjective.getName().equals("Lives")) {
                ScoreboardUtils.setObjectiveInSlot(ScoreboardDisplaySlot.LIST, null);
            }
        }
    }

    public void reload() {
        Session.MUTE_DEAD_PLAYERS = seriesConfig.MUTE_DEAD_PLAYERS.get(seriesConfig);
        SHOW_DEATH_TITLE = seriesConfig.FINAL_DEATH_TITLE_SHOW.get(seriesConfig);
        GIVELIFE_MAX_LIVES = seriesConfig.GIVELIFE_LIVES_MAX.get(seriesConfig);
        TAB_LIST_SHOW_LIVES = seriesConfig.TAB_LIST_SHOW_LIVES.get(seriesConfig);
        TAB_LIST_SHOW_DEAD_PLAYERS = seriesConfig.TAB_LIST_SHOW_DEAD_PLAYERS.get(seriesConfig);
        createTeams();
        createScoreboards();
        updateStuff();
        reloadAllPlayerTeams();
        reloadPlayers();
        Events.updatePlayerListsNextTick = true;
    }

    public void reloadPlayers() {
        for (ServerPlayerEntity player : PlayerUtils.getAllPlayers()) {
            AttributeUtils.resetAttributesOnPlayerJoin(player);
        }
    }

    public void createTeams() {
        Collection<Team> allTeams = TeamUtils.getAllTeams();
        if (allTeams != null) {
            for (Team team : allTeams) {
                if (team.getName().startsWith("creaking_")) {
                    TeamUtils.deleteTeam(team.getName());
                }
            }
        }

        TeamUtils.createTeam("Dead", Formatting.DARK_GRAY);
        TeamUtils.createTeam("Unassigned", Formatting.GRAY);

        TeamUtils.createTeam("Red", Formatting.RED);
        TeamUtils.createTeam("Yellow", Formatting.YELLOW);
        TeamUtils.createTeam("Green", Formatting.GREEN);
        TeamUtils.createTeam("DarkGreen", Formatting.DARK_GREEN);
    }

    public Formatting getColorForLives(Integer lives) {
        if (lives == null) return Formatting.GRAY;
        if (lives == 1) return Formatting.RED;
        if (lives == 2) return Formatting.YELLOW;
        if (lives == 3) return Formatting.GREEN;
        if (lives >= 4) return Formatting.DARK_GREEN;
        return Formatting.DARK_GRAY;
    }

    public Text getFormattedLives(ServerPlayerEntity player) {
        return getFormattedLives(getPlayerLives(player));
    }

    public Text getFormattedLives(Integer lives) {
        if (lives == null) return Text.empty();
        Formatting color = getColorForLives(lives);
        return Text.literal(String.valueOf(lives)).formatted(color);
    }

    public void createScoreboards() {
        ScoreboardUtils.createObjective("Lives");
    }

    public void reloadAllPlayerTeams() {
        for (ServerPlayerEntity player : PlayerUtils.getAllPlayers()) {
            reloadPlayerTeam(player);
        }
    }

    public void reloadPlayerTeam(ServerPlayerEntity player) {
        if (player == null) return;
        if (!player.isDead()) {
            reloadPlayerTeamActual(player);
        }
        else {
            TaskScheduler.scheduleTask(2, () -> reloadPlayerTeamActual(player));
        }
    }

    public void reloadPlayerTeamActual(ServerPlayerEntity player) {
        Integer lives = getPlayerLives(player);
        if (lives == null) TeamUtils.addEntityToTeam("Unassigned",player);
        else if (lives <= 0) TeamUtils.addEntityToTeam("Dead",player);
        else if (lives == 1) TeamUtils.addEntityToTeam("Red",player);
        else if (lives == 2) TeamUtils.addEntityToTeam("Yellow",player);
        else if (lives == 3) TeamUtils.addEntityToTeam("Green",player);
        else if (lives >= 4) TeamUtils.addEntityToTeam("DarkGreen",player);
        if (currentSeries.getSeries() == SeriesList.WILD_LIFE) WildLife.changedPlayerTeam(player);
        Events.updatePlayerListsNextTick = true;
    }

    public Integer getPlayerLives(ServerPlayerEntity player) {
        return ScoreboardUtils.getScore(ScoreHolder.fromName(player.getNameForScoreboard()), "Lives");
    }

    public boolean hasAssignedLives(ServerPlayerEntity player) {
        Integer lives = getPlayerLives(player);
        return lives != null;
    }

    public boolean isAlive(ServerPlayerEntity player) {
        Integer lives = getPlayerLives(player);
        if (!hasAssignedLives(player)) return false;
        return lives > 0;
    }

    public void removePlayerLife(ServerPlayerEntity player) {
        addToPlayerLives(player,-1);
    }

    public void resetPlayerLife(ServerPlayerEntity player) {
        ScoreboardUtils.resetScore(ScoreHolder.fromName(player.getNameForScoreboard()), "Lives");
        reloadPlayerTeam(player);
    }

    public void resetAllPlayerLives() {
        ScoreboardUtils.removeObjective("Lives");
        createScoreboards();
        currentSeries.reloadAllPlayerTeams();
    }

    public void addPlayerLife(ServerPlayerEntity player) {
        addToPlayerLives(player,1);
    }

    public void addToPlayerLives(ServerPlayerEntity player, int amount) {
        Integer currentLives = getPlayerLives(player);
        if (currentLives == null) currentLives = 0;
        int lives = currentLives + amount;
        if (lives < 0 && !Necromancy.isRessurectedPlayer(player)) lives = 0;
        setPlayerLives(player, lives);
    }

    public void addToLifeNoUpdate(ServerPlayerEntity player) {
        Integer currentLives = getPlayerLives(player);
        if (currentLives == null) currentLives = 0;
        int lives = currentLives + 1;
        if (lives < 0) lives = 0;
        ScoreboardUtils.setScore(ScoreHolder.fromName(player.getNameForScoreboard()), "Lives", lives);
    }

    public void receiveLifeFromOtherPlayer(Text playerName, ServerPlayerEntity target) {
        target.playSoundToPlayer(SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.MASTER, 10, 1);
        target.sendMessage(Text.literal("You received a life from ").append(playerName));
        PlayerUtils.sendTitleWithSubtitle(target, Text.of("You received a life"), Text.literal("from ").append(playerName), 10, 30, 10);
        AnimationUtils.createSpiral(target, 175);
        currentSeries.reloadPlayerTeam(target);
        SessionTranscript.givelife(playerName, target);
    }

    public void setPlayerLives(ServerPlayerEntity player, int lives) {
        Integer livesBefore = getPlayerLives(player);
        ScoreboardUtils.setScore(ScoreHolder.fromName(player.getNameForScoreboard()), "Lives", lives);
        if (lives <= 0) {
            playerLostAllLives(player, livesBefore);
        }
        else if (player.isSpectator()) {
            player.changeGameMode(GameMode.SURVIVAL);
        }
        reloadPlayerTeam(player);
    }

    @Nullable
    public Boolean isOnLastLife(ServerPlayerEntity player) {
        if (!isAlive(player)) return null;
        Integer lives = currentSeries.getPlayerLives(player);
        return lives == 1;
    }

    public boolean isOnLastLife(ServerPlayerEntity player, boolean fallback) {
        Boolean isOnLastLife = isOnLastLife(player);
        if (isOnLastLife == null) return fallback;
        return isOnLastLife;
    }

    @Nullable
    public Boolean isOnSpecificLives(ServerPlayerEntity player, int check) {
        if (!isAlive(player)) return null;
        Integer lives = currentSeries.getPlayerLives(player);
        return lives == check;
    }

    public boolean isOnSpecificLives(ServerPlayerEntity player, int check, boolean fallback) {
        Boolean isOnLife = isOnSpecificLives(player, check);
        if (isOnLife == null) return fallback;
        return isOnLife;
    }

    @Nullable
    public Boolean isOnAtLeastLives(ServerPlayerEntity player, int check) {
        if (!isAlive(player)) return null;
        Integer lives = currentSeries.getPlayerLives(player);
        return lives >= check;
    }

    public boolean isOnAtLeastLives(ServerPlayerEntity player, int check, boolean fallback) {
        Boolean isOnAtLeast = isOnAtLeastLives(player, check);
        if (isOnAtLeast == null) return fallback;
        return isOnAtLeast;
    }


    private final HashMap<UUID, HashMap<Vec3d,List<Float>>> respawnPositions = new HashMap<>();
    public void playerLostAllLives(ServerPlayerEntity player, Integer livesBefore) {
        player.changeGameMode(GameMode.SPECTATOR);
        Vec3d pos = player.getPos();
        HashMap<Vec3d, List<Float>> info = new HashMap<>();
        info.put(pos, List.of(player.getYaw(),player.getPitch()));
        respawnPositions.put(player.getUuid(), info);
        dropItemsOnLastDeath(player);
        if (livesBefore > 0) {
            PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER);
            WorldUitls.summonHarmlessLightning(PlayerUtils.getServerWorld(player), player.getPos());
            showDeathTitle(player);
        }
        SessionTranscript.onPlayerLostAllLives(player);
    }

    public void dropItemsOnLastDeath(ServerPlayerEntity player) {
        boolean doDrop = seriesConfig.PLAYERS_DROP_ITEMS_ON_FINAL_DEATH.get(seriesConfig);
        boolean keepInventory = player.getServer().getGameRules().getBoolean(GameRules.KEEP_INVENTORY);
        if (doDrop && keepInventory) {
            for (ItemStack item : PlayerUtils.getPlayerInventory(player)) {
                //? if <= 1.21 {
                player.dropStack(item);
                //?} else
                /*player.dropStack(PlayerUtils.getServerWorld(player), item);*/
            }
            player.getInventory().clear();
        }
    }

    public void showDeathTitle(ServerPlayerEntity player) {
        if (SHOW_DEATH_TITLE) {
            String subtitle = seriesConfig.FINAL_DEATH_TITLE_SUBTITLE.get(seriesConfig);
            PlayerUtils.sendTitleWithSubtitleToPlayers(PlayerUtils.getAllPlayers(), player.getStyledDisplayName(), Text.literal(subtitle), 20, 80, 20);
        }
        String message = seriesConfig.FINAL_DEATH_MESSAGE.get(seriesConfig);
        if (message.contains("${player}")) {
            String before = message.split("\\$\\{player}")[0];
            String after = message.split("\\$\\{player}")[1];
            OtherUtils.broadcastMessage(Text.literal(before).append(player.getStyledDisplayName()).append(Text.of(after)));
        }
        else {
            OtherUtils.broadcastMessage(Text.literal(message));
        }
    }

    public boolean isAllowedToAttack(ServerPlayerEntity attacker, ServerPlayerEntity victim) {
        if (isOnLastLife(attacker, false)) return true;
        return attacker.getPrimeAdversary() == victim && (isOnLastLife(victim, false));
    }

    public List<ServerPlayerEntity> getNonRedPlayers() {
        List<ServerPlayerEntity> players = PlayerUtils.getAllPlayers();
        if (players.isEmpty()) return new ArrayList<>();
        List<ServerPlayerEntity> nonRedPlayers = new ArrayList<>();
        for (ServerPlayerEntity player : players) {
            Boolean isOnLastLife = currentSeries.isOnLastLife(player);
            if (isOnLastLife == null) continue;
            if (isOnLastLife) continue;
            nonRedPlayers.add(player);
        }
        return nonRedPlayers;
    }

    public List<ServerPlayerEntity> getAlivePlayers() {
        List<ServerPlayerEntity> players = PlayerUtils.getAllPlayers();
        if (players.isEmpty()) return new ArrayList<>();
        List<ServerPlayerEntity> alivePlayers = new ArrayList<>();
        for (ServerPlayerEntity player : players) {
            if (!isAlive(player)) continue;
            alivePlayers.add(player);
        }
        return alivePlayers;
    }

    public boolean anyGreenPlayers() {
        for (ServerPlayerEntity player : PlayerUtils.getAllPlayers()) {
            if (isOnSpecificLives(player, 3, false)) return true;
        }
        return false;
    }

    public boolean anyYellowPlayers() {
        for (ServerPlayerEntity player : PlayerUtils.getAllPlayers()) {
            if (isOnSpecificLives(player, 2, false)) return true;
        }
        return false;
    }
    /*
        Events
     */

    public void onPlayerDeath(ServerPlayerEntity player, DamageSource source) {
        SessionTranscript.onPlayerDeath(player, source);
        boolean killedByPlayer = false;
        if (source.getAttacker() instanceof ServerPlayerEntity serverAttacker) {
            if (player != source.getAttacker()) {
                onPlayerKilledByPlayer(player, serverAttacker);
                killedByPlayer = true;
            }
        }
        if (player.getPrimeAdversary() != null && !killedByPlayer) {
            if (player.getPrimeAdversary() instanceof ServerPlayerEntity serverAdversary) {
                if (player != player.getPrimeAdversary()) {
                    onPlayerKilledByPlayer(player, serverAdversary);
                    killedByPlayer = true;
                }
            }
        }
        if (!killedByPlayer) {
            onPlayerDiedNaturally(player);
        }
        removePlayerLife(player);
    }

    public void onPlayerDiedNaturally(ServerPlayerEntity player) {
        if (server == null) return;
        playerNaturalDeathLog.remove(player.getUuid());
        playerNaturalDeathLog.put(player.getUuid(), server.getTicks());
    }

    public void onPlayerRespawn(ServerPlayerEntity player) {
        if (!respawnPositions.containsKey(player.getUuid())) return;
        HashMap<Vec3d, List<Float>> info = respawnPositions.get(player.getUuid());
        respawnPositions.remove(player.getUuid());
        for (Map.Entry<Vec3d, List<Float>> entry : info.entrySet()) {
            //? if <= 1.21 {
            player.teleport(PlayerUtils.getServerWorld(player), entry.getKey().x, entry.getKey().y, entry.getKey().z, EnumSet.noneOf(PositionFlag.class), entry.getValue().get(0), entry.getValue().get(1));
            //?} else {
            /*player.teleport(PlayerUtils.getServerWorld(player), entry.getKey().x, entry.getKey().y, entry.getKey().z, EnumSet.noneOf(PositionFlag.class), entry.getValue().get(0), entry.getValue().get(1), false);
            *///?}
            break;
        }
    }

    public void onClaimKill(ServerPlayerEntity killer, ServerPlayerEntity victim) {
        SessionTranscript.claimKill(killer, victim);
    }

    public void onPlayerDamage(ServerPlayerEntity player, DamageSource source, float amount, CallbackInfo ci) {
    }

    public void onPrePlayerDamage(ServerPlayerEntity player, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
    }

    public void onPlayerHeal(ServerPlayerEntity player, float amount) {
    }

    public void onPlayerKilledByPlayer(ServerPlayerEntity victim, ServerPlayerEntity killer) {
    }

    public void onMobDeath(LivingEntity entity, DamageSource damageSource) {
    }

    public void onEntityDropItems(LivingEntity entity, DamageSource damageSource) {
        modifyEntityDrops(entity, damageSource);
    }

    public void modifyEntityDrops(LivingEntity entity, DamageSource damageSource) {
        if (!entity.getWorld().isClient() && (damageSource.getAttacker() instanceof ServerPlayerEntity)) {
            spawnEggChance(entity);
        }
    }

    private void spawnEggChance(LivingEntity entity) {
        double chance = seriesConfig.SPAWN_EGG_DROP_CHANCE.get(seriesConfig);
        boolean onlyNatural = seriesConfig.SPAWN_EGG_DROP_ONLY_NATURAL.get(seriesConfig);
        if (chance <= 0) return;
        if (entity instanceof EnderDragonEntity) return;
        if (entity instanceof WitherEntity) return;
        if (entity instanceof WardenEntity) return;
        if (entity instanceof ElderGuardianEntity) return;
        if (entity instanceof Snail) return;
        if (entity instanceof TriviaBot) return;
        if (entity.getCommandTags().contains("notNatural") && onlyNatural) return;

        EntityType<?> entityType = entity.getType();
        SpawnEggItem spawnEgg = SpawnEggItem.forEntity(entityType);


        if (spawnEgg == null) return;
        ItemStack spawnEggItem = spawnEgg.getDefaultStack();
        if (spawnEggItem == null) return;
        if (spawnEggItem.isEmpty()) return;

        // Drop the spawn egg with a 5% chance
        if (Math.random() <= chance) {
            //? if <=1.21 {
            entity.dropStack(spawnEggItem);
            //?} else
            /*entity.dropStack((ServerWorld) entity.getWorld(), spawnEggItem);*/
        }
    }

    public void learnRecipes() {
        OtherUtils.executeCommand("recipe give @a lifeseries:name_tag_recipe");
        OtherUtils.executeCommand("recipe give @a lifeseries:saddle_recipe");
        OtherUtils.executeCommand("recipe give @a lifeseries:spawner_recipe");
        OtherUtils.executeCommand("recipe give @a lifeseries:tnt_recipe_variation");
        OtherUtils.executeCommand("recipe give @a lifeseries:bundle_recipe");
    }

    public void onPlayerJoin(ServerPlayerEntity player) {
        AttributeUtils.resetAttributesOnPlayerJoin(player);
        reloadPlayerTeam(player);
        TaskScheduler.scheduleTask(2, () -> PlayerUtils.applyResourcepack(player.getUuid()));
    }

    public void onPlayerFinishJoining(ServerPlayerEntity player) {
        learnRecipes();
        if (statusNotStarted() && PermissionManager.isAdmin(player)) {
            player.sendMessage(Text.of("\nUse §b'/session timer set <time>'§f to set the desired session time."));
            player.sendMessage(Text.of("After that, use §b'/session start'§f to start the session."));
        }
    }


    public void onPlayerDisconnect(ServerPlayerEntity player) {
    }

    public void onRightClickEntity(ServerPlayerEntity player, World world, Hand hand, Entity entity, EntityHitResult hitResult) {
    }

    public void onAttackEntity(ServerPlayerEntity player, World world, Hand hand, Entity entity, EntityHitResult hitResult) {
    }
}
