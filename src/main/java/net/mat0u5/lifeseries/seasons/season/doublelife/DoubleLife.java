package net.mat0u5.lifeseries.seasons.season.doublelife;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.config.ConfigManager;
import net.mat0u5.lifeseries.config.StringListConfig;
import net.mat0u5.lifeseries.seasons.boogeyman.Boogeyman;
import net.mat0u5.lifeseries.seasons.season.Season;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.session.SessionAction;
import net.mat0u5.lifeseries.seasons.session.SessionTranscript;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.TaskScheduler;
import net.mat0u5.lifeseries.utils.player.PermissionManager;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.border.WorldBorder;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

import static net.mat0u5.lifeseries.Main.*;

public class DoubleLife extends Season {
    public static final String COMMANDS_ADMIN_TEXT = "/lifeseries, /session, /claimkill, /lives, /soulmate";
    public static final String COMMANDS_TEXT = "/claimkill, /lives";
    public static final RegistryKey<DamageType> SOULMATE_DAMAGE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(Main.MOD_ID, "soulmate"));
    StringListConfig soulmateConfig;

    public SessionAction actionChooseSoulmates = new SessionAction(
            OtherUtils.minutesToTicks(1), "§7Assign soulmates if necessary §f[00:01:00]", "Assign Soulmates if necessary"
    ) {
        @Override
        public void trigger() {
            rollSoulmates();
        }
    };
    public SessionAction actionRandomTP = new SessionAction(5, "§7Random teleport distribution §f[00:00:01]", "Random teleport distribution") {
        @Override
        public void trigger() {
            distributePlayers();
        }
    };

    public Map<UUID, UUID> soulmates = new HashMap<>();
    public Map<UUID, UUID> soulmatesOrdered = new HashMap<>();

    @Override
    public void initialize() {
        super.initialize();
        soulmateConfig = new StringListConfig("./config/lifeseries/main", "DO_NOT_MODIFY_doublelife_soulmates.properties");
    }

    @Override
    public Seasons getSeason() {
        return Seasons.DOUBLE_LIFE;
    }

    @Override
    public ConfigManager getConfig() {
        return new DoubleLifeConfig();
    }

    @Override
    public void onPlayerJoin(ServerPlayerEntity player) {
        super.onPlayerJoin(player);

        if (!hasAssignedLives(player)) {
            int lives = seasonConfig.DEFAULT_LIVES.get(seasonConfig);
            setPlayerLives(player, lives);
        }

        if (player == null) return;
        if (!hasSoulmate(player)) return;
        if (!isSoulmateOnline(player)) return;

        ServerPlayerEntity soulmate = getSoulmate(player);
        syncPlayers(player, soulmate);
    }

    @Override
    public void onPlayerFinishJoining(ServerPlayerEntity player) {
        if (PermissionManager.isAdmin(player)) {
            player.sendMessage(Text.of("§7Double Life commands: §r"+COMMANDS_ADMIN_TEXT));
        }
        else {
            player.sendMessage(Text.of("§7Double Life non-admin commands: §r"+COMMANDS_TEXT));
        }
        super.onPlayerFinishJoining(player);
    }

    @Override
    public boolean sessionStart() {
        if (super.sessionStart()) {
            activeActions.addAll(
                    List.of(actionChooseSoulmates, actionRandomTP)
            );
            return true;
        }
        return false;
    }
    @Override
    public boolean isAllowedToAttack(ServerPlayerEntity attacker, ServerPlayerEntity victim) {
        ServerPlayerEntity soulmate = getSoulmate(victim);
        if (soulmate != null && soulmate == attacker) return true;
        return super.isAllowedToAttack(attacker, victim);
    }

    @Override
    public void onPlayerRespawn(ServerPlayerEntity player) {
        super.onPlayerRespawn(player);
        ServerPlayerEntity soulmate = getSoulmate(player);
        if (soulmate == null) return;
        syncPlayers(player, soulmate);
    }

    public void loadSoulmates() {
        soulmates = getAllSoulmates();
        updateOrderedSoulmates();
    }

    public void updateOrderedSoulmates() {
        soulmatesOrdered = new HashMap<>();
        for (Map.Entry<UUID, UUID> entry : soulmates.entrySet()) {
            if (soulmatesOrdered.containsKey(entry.getKey()) || soulmatesOrdered.containsValue(entry.getKey())) continue;
            if (soulmatesOrdered.containsKey(entry.getValue()) || soulmatesOrdered.containsValue(entry.getValue())) continue;
            soulmatesOrdered.put(entry.getKey(),entry.getValue());
        }
    }

    public void saveSoulmates() {
        updateOrderedSoulmates();
        setAllSoulmates(soulmatesOrdered);
    }

    public boolean isMainSoulmate(ServerPlayerEntity player) {
        return soulmatesOrdered.containsKey(player.getUuid());
    }

    public boolean hasSoulmate(ServerPlayerEntity player) {
        if (player == null) return false;
        UUID playerUUID = player.getUuid();
        return soulmates.containsKey(playerUUID);
    }

    public boolean isSoulmateOnline(ServerPlayerEntity player) {
        if (server == null) return false;
        if (!hasSoulmate(player)) return false;
        UUID soulmateUUID = soulmates.get(player.getUuid());
        return PlayerUtils.getPlayer(soulmateUUID) != null;
    }

    public ServerPlayerEntity getSoulmate(ServerPlayerEntity player) {
        if (server == null) return null;
        if (!isSoulmateOnline(player)) return null;
        UUID soulmateUUID = soulmates.get(player.getUuid());
        return PlayerUtils.getPlayer(soulmateUUID);
    }

    public void setSoulmate(ServerPlayerEntity player1, ServerPlayerEntity player2) {
        soulmates.put(player1.getUuid(), player2.getUuid());
        soulmates.put(player2.getUuid(), player1.getUuid());
        SessionTranscript.soulmate(player1, player2);
        syncPlayers(player1, player2);
        updateOrderedSoulmates();
    }

    public void resetSoulmate(ServerPlayerEntity player) {
        UUID playerUUID = player.getUuid();
        Map<UUID, UUID> newSoulmates = new HashMap<>();
        for (Map.Entry<UUID, UUID> entry : soulmates.entrySet()) {
            if (entry.getKey().equals(playerUUID)) continue;
            if (entry.getValue().equals(playerUUID)) continue;
            newSoulmates.put(entry.getKey(), entry.getValue());
        }
        soulmates = newSoulmates;
        updateOrderedSoulmates();
    }

    public void resetAllSoulmates() {
        soulmates = new HashMap<>();
        soulmatesOrdered = new HashMap<>();
        soulmateConfig.resetProperties("-- DO NOT MODIFY --");
    }

    public void rollSoulmates() {
        List<ServerPlayerEntity> playersToRoll = getNonAssignedPlayers();
        PlayerUtils.playSoundToPlayers(playersToRoll, SoundEvents.UI_BUTTON_CLICK.value());
        PlayerUtils.sendTitleToPlayers(playersToRoll, Text.literal("3").formatted(Formatting.GREEN),5,20,5);
        TaskScheduler.scheduleTask(25, () -> {
            PlayerUtils.playSoundToPlayers(playersToRoll, SoundEvents.UI_BUTTON_CLICK.value());
            PlayerUtils.sendTitleToPlayers(playersToRoll, Text.literal("2").formatted(Formatting.GREEN),5,20,5);
        });
        TaskScheduler.scheduleTask(50, () -> {
            PlayerUtils.playSoundToPlayers(playersToRoll, SoundEvents.UI_BUTTON_CLICK.value());
            PlayerUtils.sendTitleToPlayers(playersToRoll, Text.literal("1").formatted(Formatting.GREEN),5,20,5);
        });
        TaskScheduler.scheduleTask(75, () -> {
            PlayerUtils.sendTitleToPlayers(playersToRoll, Text.literal("Your soulmate is...").formatted(Formatting.GREEN),10,50,20);
            PlayerUtils.playSoundToPlayers(playersToRoll, SoundEvent.of(Identifier.of("minecraft","doublelife_soulmate_wait")));
        });
        TaskScheduler.scheduleTask(165, () -> {
            PlayerUtils.sendTitleToPlayers(playersToRoll, Text.literal("????").formatted(Formatting.GREEN),20,60,20);
            PlayerUtils.playSoundToPlayers(playersToRoll, SoundEvent.of(Identifier.of("minecraft","doublelife_soulmate_chosen")));
            chooseRandomSoulmates();
        });
    }

    public List<ServerPlayerEntity> getNonAssignedPlayers() {
        List<ServerPlayerEntity> playersToRoll = new ArrayList<>();
        for (ServerPlayerEntity player : PlayerUtils.getAllPlayers()) {
            if (!isAlive(player)) continue;
            if (hasSoulmate(player)) continue;
            playersToRoll.add(player);
        }
        return playersToRoll;
    }

    public void distributePlayers() {
        if (server == null) return;
        List<ServerPlayerEntity> players = getNonAssignedPlayers();
        if (players.isEmpty()) return;
        if (players.size() == 1) return;
        PlayerUtils.playSoundToPlayers(players, SoundEvents.ENTITY_ENDERMAN_TELEPORT);

        for (ServerPlayerEntity player : PlayerUtils.getAllPlayers()) {
            player.removeCommandTag("randomTeleport");
        }

        for (ServerPlayerEntity player : players) {
            player.addCommandTag("randomTeleport");
            player.sendMessage(Text.of("§6Woosh!"));
        }
        WorldBorder border = server.getOverworld().getWorldBorder();
        OtherUtils.executeCommand("spreadplayers " + border.getCenterX() + " " + border.getCenterZ() + " 0 " + (border.getSize()/2) + " false @a[tag=randomTeleport]");
        OtherUtils.broadcastMessageToAdmins(Text.of("Randomly distributed players."));

        for (ServerPlayerEntity player : PlayerUtils.getAllPlayers()) {
            player.removeCommandTag("randomTeleport");
        }
    }

    public void chooseRandomSoulmates() {
        List<ServerPlayerEntity> playersToRoll = getNonAssignedPlayers();
        Collections.shuffle(playersToRoll);
        if (playersToRoll.size()%2 != 0) {
            ServerPlayerEntity remove = playersToRoll.getFirst();
            playersToRoll.remove(remove);
            OtherUtils.broadcastMessageToAdmins(Text.literal(" [DoubleLife] ").append(remove.getStyledDisplayName()).append(" was not paired with anyone, as there is an odd number of non-assigned players online."));
        }
        while(!playersToRoll.isEmpty()) {
            ServerPlayerEntity player1 = playersToRoll.get(0);
            ServerPlayerEntity player2 = playersToRoll.get(1);
            setSoulmate(player1,player2);
            playersToRoll.removeFirst();
            playersToRoll.removeFirst();
        }
        saveSoulmates();
    }

    @Override
    public void onPlayerHeal(ServerPlayerEntity player, float amount) {
        if (player == null) return;
        if (!hasSoulmate(player)) return;
        if (!isSoulmateOnline(player)) return;

        ServerPlayerEntity soulmate = getSoulmate(player);
        if (soulmate == null) return;
        if (soulmate.isDead()) return;

        float newHealth = Math.min(soulmate.getHealth() + amount, soulmate.getMaxHealth());
        soulmate.setHealth(newHealth);
        TaskScheduler.scheduleTask(1,()-> syncPlayers(player, soulmate));
    }

    @Override
    public void onPlayerDamage(ServerPlayerEntity player, DamageSource source, float amount, CallbackInfo ci) {
        if (source.getType().msgId().equalsIgnoreCase("soulmate")) return;
        if (player == null) return;
        if (!hasSoulmate(player)) return;
        if (!isSoulmateOnline(player)) return;

        ServerPlayerEntity soulmate = getSoulmate(player);
        if (soulmate == null) return;
        if (soulmate.isDead()) return;

        if (soulmate.hurtTime == 0) {
            //? if <=1.21 {
            DamageSource damageSource = new DamageSource( soulmate.getWorld().getRegistryManager()
                    .get(RegistryKeys.DAMAGE_TYPE).entryOf(SOULMATE_DAMAGE));
            soulmate.damage(damageSource, 0.0000001F);
            //?} else {
            /*DamageSource damageSource = new DamageSource( soulmate.getWorld().getRegistryManager()
                    .getOrThrow(RegistryKeys.DAMAGE_TYPE).getOrThrow(SOULMATE_DAMAGE));
            soulmate.damage(PlayerUtils.getServerWorld(soulmate), damageSource, 0.0000001F);
            *///?}
        }

        float newHealth = player.getHealth();
        if (newHealth <= 0.0F) newHealth = 0.01F;
        soulmate.setHealth(newHealth);

        TaskScheduler.scheduleTask(1,() -> syncPlayers(player, soulmate));
    }

    @Override
    public void onPlayerDeath(ServerPlayerEntity player, DamageSource source) {
        super.onPlayerDeath(player, source);

        if (player == null) return;
        if (!hasSoulmate(player)) return;
        if (!isSoulmateOnline(player)) return;

        ServerPlayerEntity soulmate = getSoulmate(player);
        if (soulmate == null) return;
        if (soulmate.isDead()) return;

        //? if <=1.21 {
        DamageSource damageSource = new DamageSource( soulmate.getWorld().getRegistryManager()
                .get(RegistryKeys.DAMAGE_TYPE).entryOf(SOULMATE_DAMAGE));
        soulmate.setAttacker(player);
        soulmate.setAttacking(player);
        soulmate.damage(damageSource, 1000);
         //?} else {
        /*DamageSource damageSource = new DamageSource( soulmate.getWorld().getRegistryManager()
                .getOrThrow(RegistryKeys.DAMAGE_TYPE).getOrThrow(SOULMATE_DAMAGE));
        soulmate.setAttacker(player);
        //? if <= 1.21.4 {
        soulmate.setAttacking(player);
        //?} else {
        /^soulmate.setAttacking(player, 100);
        ^///?}
        soulmate.damage(PlayerUtils.getServerWorld(soulmate), damageSource, 1000);
        *///?}

    }

    public void syncPlayers(ServerPlayerEntity player, ServerPlayerEntity soulmate) {
        if (player.isDead() || soulmate.isDead()) return;
        if (player.getHealth() != soulmate.getHealth()) {
            float sharedHealth = Math.min(player.getHealth(), soulmate.getHealth());
            if (sharedHealth != 0.0F) {
                player.setHealth(sharedHealth);
                soulmate.setHealth(sharedHealth);
            }
        }
        
        Integer soulmateLives = getPlayerLives(soulmate);
        Integer playerLives = getPlayerLives(player);
        if (soulmateLives != null && playerLives != null)  {
            if (!Objects.equals(soulmateLives, playerLives)) {
                int minLives = Math.min(soulmateLives,playerLives);
                setPlayerLives(player, minLives);
                setPlayerLives(soulmate, minLives);
            }
        }
    }

    public void canFoodHeal(ServerPlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        boolean orig =  player.getHealth() > 0.0F && player.getHealth() < player.getMaxHealth();
        if (!orig) {
            cir.setReturnValue(false);
            return;
        }

        DoubleLife doubleLife = ((DoubleLife) currentSeason);
        if (!doubleLife.hasSoulmate(player)) return;
        if (!doubleLife.isSoulmateOnline(player)) return;
        if (doubleLife.isMainSoulmate(player)) return;
        ServerPlayerEntity soulmate = doubleLife.getSoulmate(player);
        if (soulmate == null) return;
        if (soulmate.isDead()) return;

        boolean canHealWithSaturationOther =  soulmate.getHungerManager().getSaturationLevel() > 2.0F && soulmate.getHungerManager().getFoodLevel() >= 20;

        if (canHealWithSaturationOther) {
            cir.setReturnValue(false);
        }
        else {
            cir.setReturnValue(true);
        }
    }

    public void setAllSoulmates(Map<UUID, UUID> soulmates) {
        List<String> list = new ArrayList<>();
        for (Map.Entry<UUID, UUID> entry : soulmates.entrySet()) {
            list.add(entry.getKey().toString()+"_"+entry.getValue().toString());
        }
        soulmateConfig.save(list);
    }

    public Map<UUID, UUID> getAllSoulmates() {
        Map<UUID, UUID> loadedSoulmates = new HashMap<>();
        List<String> list = soulmateConfig.load();
        for (String str : list) {
            try {
                if (!str.contains("_")) continue;
                String[] split = str.split("_");
                if (split.length != 2) continue;
                UUID key = UUID.fromString(split[0]);
                UUID value = UUID.fromString(split[1]);
                loadedSoulmates.put(key, value);
                loadedSoulmates.put(value, key);
            }catch(Exception ignored) {}
        }
        return loadedSoulmates;
    }
}
