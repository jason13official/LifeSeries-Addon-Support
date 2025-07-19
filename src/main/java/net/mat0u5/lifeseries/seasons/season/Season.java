package net.mat0u5.lifeseries.seasons.season;

import net.mat0u5.lifeseries.config.ConfigManager;
import net.mat0u5.lifeseries.entity.snail.Snail;
import net.mat0u5.lifeseries.entity.triviabot.TriviaBot;
import net.mat0u5.lifeseries.events.Events;
import net.mat0u5.lifeseries.seasons.blacklist.Blacklist;
import net.mat0u5.lifeseries.seasons.boogeyman.Boogeyman;
import net.mat0u5.lifeseries.seasons.boogeyman.BoogeymanManager;
import net.mat0u5.lifeseries.seasons.season.wildlife.WildLife;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower.Necromancy;
import net.mat0u5.lifeseries.seasons.session.Session;
import net.mat0u5.lifeseries.seasons.session.SessionTranscript;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.TaskScheduler;
import net.mat0u5.lifeseries.utils.player.*;
import net.mat0u5.lifeseries.utils.world.AnimationUtils;
import net.mat0u5.lifeseries.utils.world.WorldUitls;
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
//? if >= 1.21.2
/*import net.minecraft.server.world.ServerWorld;*/

public abstract class Season extends Session {
    public static final String RESOURCEPACK_MAIN_URL = "https://github.com/Mat0u5/LifeSeries-Resources/releases/download/release-main-a2267cdefcc227356dfa14261923a140cb4635e6/main.zip";
    public static final String RESOURCEPACK_MAIN_SHA ="328550e43f517a5ed26a0a9597c255d15783645e";
    public static final String RESOURCEPACK_SECRETLIFE_URL = "https://github.com/Mat0u5/LifeSeries-Resources/releases/download/release-secretlife-4ef5fb2c497037dc9f18437ec8788eac5e01dbab/secretlife.zip";
    public static final String RESOURCEPACK_SECRETLIFE_SHA ="92a7c3dfc6641509de72a7c687a3707ba3843e6c";
    public static final String RESOURCEPACK_MINIMAL_ARMOR_URL = "https://github.com/Mat0u5/LifeSeries-Resources/releases/download/release-minimal_armor-4ef5fb2c497037dc9f18437ec8788eac5e01dbab/minimal_armor.zip";
    public static final String RESOURCEPACK_MINIMAL_ARMOR_SHA ="d5e9e21ab788974ef3a58bd8b14ccc7d34ea422c";

    public boolean NO_HEALING = false;
    public boolean SHOW_DEATH_TITLE = false;
    public int GIVELIFE_MAX_LIVES = 99;
    public boolean TAB_LIST_SHOW_DEAD_PLAYERS = true;
    public boolean TAB_LIST_SHOW_LIVES = false;

    public BoogeymanManager boogeymanManagerNew = createBoogeymanManager();

    public abstract Seasons getSeason();
    public abstract ConfigManager getConfig();

    public Blacklist createBlacklist() {
        return new Blacklist();
    }

    public BoogeymanManager createBoogeymanManager() {
        return new BoogeymanManager();
    }

    public void initialize() {
        reload();
    }

    public void updateStuff() {
        if (server == null) return;
        if (server.getOverworld().getWorldBorder().getSize() > 1000000 && seasonConfig.AUTO_SET_WORLDBORDER.get(seasonConfig)) {
            OtherUtils.executeCommand("worldborder set 500");
        }

        server.getGameRules().get(GameRules.KEEP_INVENTORY).set(seasonConfig.KEEP_INVENTORY.get(seasonConfig), server);
        server.getGameRules().get(GameRules.NATURAL_REGENERATION).set(!NO_HEALING, server);
        //? if >= 1.21.6 {
        /*server.getGameRules().get(GameRules.LOCATOR_BAR).set(seasonConfig.LOCATOR_BAR.get(seasonConfig), server);
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
        Session.MUTE_DEAD_PLAYERS = seasonConfig.MUTE_DEAD_PLAYERS.get(seasonConfig);
        SHOW_DEATH_TITLE = seasonConfig.FINAL_DEATH_TITLE_SHOW.get(seasonConfig);
        GIVELIFE_MAX_LIVES = seasonConfig.GIVELIFE_LIVES_MAX.get(seasonConfig);
        TAB_LIST_SHOW_LIVES = seasonConfig.TAB_LIST_SHOW_LIVES.get(seasonConfig);
        TAB_LIST_SHOW_DEAD_PLAYERS = seasonConfig.TAB_LIST_SHOW_DEAD_PLAYERS.get(seasonConfig);

        boogeymanManagerNew.onReload();
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
        if (currentSeason.getSeason() == Seasons.WILD_LIFE) WildLife.changedPlayerTeam(player);
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
        currentSeason.reloadAllPlayerTeams();
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
        currentSeason.reloadPlayerTeam(target);
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
        Integer lives = currentSeason.getPlayerLives(player);
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
        Integer lives = currentSeason.getPlayerLives(player);
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
        Integer lives = currentSeason.getPlayerLives(player);
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
        boogeymanManagerNew.playerLostAllLives(player);
    }

    public void dropItemsOnLastDeath(ServerPlayerEntity player) {
        boolean doDrop = seasonConfig.PLAYERS_DROP_ITEMS_ON_FINAL_DEATH.get(seasonConfig);
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
            String subtitle = seasonConfig.FINAL_DEATH_TITLE_SUBTITLE.get(seasonConfig);
            PlayerUtils.sendTitleWithSubtitleToPlayers(PlayerUtils.getAllPlayers(), player.getStyledDisplayName(), Text.literal(subtitle), 20, 80, 20);
        }
        String message = seasonConfig.FINAL_DEATH_MESSAGE.get(seasonConfig);
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
        if (attacker.getPrimeAdversary() == victim && isOnLastLife(victim, false)) return true;
        Boogeyman boogeymanAttacker = boogeymanManagerNew.getBoogeyman(attacker);
        Boogeyman boogeymanVictim = boogeymanManagerNew.getBoogeyman(victim);
        if (boogeymanAttacker != null && !boogeymanAttacker.cured) return true;
        return attacker.getPrimeAdversary() == victim && (boogeymanVictim != null && !boogeymanVictim.cured);
    }

    public List<ServerPlayerEntity> getNonRedPlayers() {
        List<ServerPlayerEntity> players = PlayerUtils.getAllPlayers();
        if (players.isEmpty()) return new ArrayList<>();
        List<ServerPlayerEntity> nonRedPlayers = new ArrayList<>();
        for (ServerPlayerEntity player : players) {
            Boolean isOnLastLife = currentSeason.isOnLastLife(player);
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

    @Override
    public void sessionEnd() {
        super.sessionEnd();
        boogeymanManagerNew.sessionEnd();
    }

    @Override
    public boolean sessionStart() {
        if (super.sessionStart()) {
            boogeymanManagerNew.resetBoogeymen();
            activeActions.addAll(List.of(
                    boogeymanManagerNew.actionBoogeymanWarn1,
                    boogeymanManagerNew.actionBoogeymanWarn2,
                    boogeymanManagerNew.actionBoogeymanChoose
            ));
            return true;
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
        if (isAlive(player)) return;
        for (Map.Entry<Vec3d, List<Float>> entry : info.entrySet()) {
            Vec3d pos = entry.getKey();
            if (pos.y <= PlayerUtils.getServerWorld(player).getBottomY()) continue;

            //? if <= 1.21 {
            player.teleport(PlayerUtils.getServerWorld(player), pos.x, pos.y, pos.z, EnumSet.noneOf(PositionFlag.class), entry.getValue().get(0), entry.getValue().get(1));
            //?} else {
            /*player.teleport(PlayerUtils.getServerWorld(player), pos.x, pos.y, pos.z, EnumSet.noneOf(PositionFlag.class), entry.getValue().get(0), entry.getValue().get(1), false);
            *///?}
            break;
        }
    }

    public void onClaimKill(ServerPlayerEntity killer, ServerPlayerEntity victim) {
        SessionTranscript.claimKill(killer, victim);
        Boogeyman boogeyman  = boogeymanManagerNew.getBoogeyman(killer);
        if (boogeyman != null && !boogeyman.cured && !isOnLastLife(victim, true)) {
            boogeymanManagerNew.cure(killer);
        }
    }

    public void onPlayerDamage(ServerPlayerEntity player, DamageSource source, float amount, CallbackInfo ci) {
    }

    public void onPrePlayerDamage(ServerPlayerEntity player, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
    }

    public void onPlayerHeal(ServerPlayerEntity player, float amount) {
    }

    public void onPlayerKilledByPlayer(ServerPlayerEntity victim, ServerPlayerEntity killer) {
        if (isAllowedToAttack(killer, victim)) {
            Boogeyman boogeyman  = boogeymanManagerNew.getBoogeyman(killer);
            if (boogeyman != null && !boogeyman.cured && !isOnLastLife(victim, true)) {
                boogeymanManagerNew.cure(killer);
            }
        }
        else {
            OtherUtils.broadcastMessageToAdmins(Text.of("§c [Unjustified Kill?] §f"+victim.getNameForScoreboard() + "§7 was killed by §f"+killer.getNameForScoreboard() +
                    "§7, who is not §cred name!"));
        }
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
        double chance = seasonConfig.SPAWN_EGG_DROP_CHANCE.get(seasonConfig);
        boolean onlyNatural = seasonConfig.SPAWN_EGG_DROP_ONLY_NATURAL.get(seasonConfig);
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
        boogeymanManagerNew.onPlayerJoin(player);
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
