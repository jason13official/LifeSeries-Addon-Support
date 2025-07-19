package net.mat0u5.lifeseries;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.mat0u5.lifeseries.config.ConfigManager;
import net.mat0u5.lifeseries.config.MainConfig;
import net.mat0u5.lifeseries.dependencies.DependencyManager;
import net.mat0u5.lifeseries.dependencies.PolymerDependency;
import net.mat0u5.lifeseries.events.Events;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.registries.ModRegistries;
import net.mat0u5.lifeseries.resources.datapack.DatapackManager;
import net.mat0u5.lifeseries.seasons.blacklist.Blacklist;
import net.mat0u5.lifeseries.seasons.season.Season;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.aprilfools.simplelife.SimpleLife;
import net.mat0u5.lifeseries.seasons.season.doublelife.DoubleLife;
import net.mat0u5.lifeseries.seasons.season.lastlife.LastLife;
import net.mat0u5.lifeseries.seasons.season.limitedlife.LimitedLife;
import net.mat0u5.lifeseries.seasons.season.secretlife.SecretLife;
import net.mat0u5.lifeseries.seasons.season.secretlife.TaskManager;
import net.mat0u5.lifeseries.seasons.season.thirdlife.ThirdLife;
import net.mat0u5.lifeseries.seasons.season.unassigned.UnassignedSeason;
import net.mat0u5.lifeseries.seasons.season.wildlife.WildLife;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.snails.SnailSkinsServer;
import net.mat0u5.lifeseries.seasons.session.Session;
import net.mat0u5.lifeseries.seasons.session.SessionTranscript;
import net.mat0u5.lifeseries.utils.interfaces.IClientHelper;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.versions.UpdateChecker;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

public class Main implements ModInitializer {
	public static final String MOD_VERSION = "dev-1.3.5.22";
	public static final String MOD_ID = "lifeseries";
	public static final String MAJOR_UPDATE_URL = "https://api.github.com/repos/Mat0u5/LifeSeries/releases/latest";
	public static final String ALL_UPDATES_URL = "https://api.github.com/repos/Mat0u5/LifeSeries/releases";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final boolean DEBUG = false;
	private static ConfigManager config;
	public static IClientHelper clientHelper;

	@Nullable
	public static MinecraftServer server;
	public static Season currentSeason;
	public static Session currentSession;
	public static Blacklist blacklist;
	public static ConfigManager seasonConfig;
	public static final List<String> ALLOWED_SEASON_NAMES = Seasons.getImplementedSeasonNames();

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing Life Series...");
		ConfigManager.moveOldMainFileIfExists();
		SnailSkinsServer.createConfig();

		if (DependencyManager.polymerLoaded()) {
			PolymerDependency.onInitialize();
		}

		config = new MainConfig();
		String season = config.getOrCreateProperty("currentSeries", "unassigned");

		parseSeason(season);
		ConfigManager.createConfigs();

		ModRegistries.registerModStuff();
		UpdateChecker.checkForMajorUpdates();

		NetworkHandlerServer.registerPackets();
		NetworkHandlerServer.registerServerReceiver();
	}


	public static boolean isClient() {
		return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
	}

	public static void setClientHelper(IClientHelper helper) {
		clientHelper = helper;
	}

	public static boolean isLogicalSide() {
		if (!isClient()) return true;
		return clientHelper != null && clientHelper.isRunningIntegratedServer();
	}

	public static boolean isClientPlayer(UUID uuid) {
		if (!isClient()) return false;
		return clientHelper != null && clientHelper.isMainClientPlayer(uuid);
	}

	public static void parseSeason(String season) {
		if (!ALLOWED_SEASON_NAMES.contains(season)) {
			currentSeason = new UnassignedSeason();
		}
		if (season.equalsIgnoreCase("thirdlife")) {
			currentSeason = new ThirdLife();
		}
		if (season.equalsIgnoreCase("lastlife")) {
			currentSeason = new LastLife();
		}
		if (season.equalsIgnoreCase("doublelife")) {
			currentSeason = new DoubleLife();
		}
		if (season.equalsIgnoreCase("limitedlife")) {
			currentSeason = new LimitedLife();
		}
		if (season.equalsIgnoreCase("secretlife")) {
			currentSeason = new SecretLife();
		}
		if (season.equalsIgnoreCase("wildlife")) {
			if (DependencyManager.polymerLoaded() && DependencyManager.wildLifeModsLoaded()) {
				currentSeason = new WildLife();
			}
			else {
				currentSeason = new UnassignedSeason();
				changeSeasonTo("unassigned");
			}
		}
		if (season.equalsIgnoreCase("simplelife")) {
			currentSeason = new SimpleLife();
		}
		currentSession = currentSeason;
		seasonConfig = currentSeason.getConfig();
		blacklist = currentSeason.createBlacklist();
	}

	public static void reloadStart() {
		if (Events.skipNextTickReload) return;
		softReloadStart();
		DatapackManager.onReloadStart();
	}

	public static void softReloadStart() {
		softestReloadStart();
		SnailSkinsServer.sendStoredImages();
	}

	public static void softestReloadStart() {
		if (currentSeason.getSeason() == Seasons.SECRET_LIFE) {
			TaskManager.initialize();
		}
		if (currentSeason.getSeason() == Seasons.DOUBLE_LIFE && currentSeason instanceof DoubleLife doubleLife) {
			doubleLife.loadSoulmates();
		}
		seasonConfig.loadProperties();
		blacklist.reloadBlacklist();
		currentSeason.reload();
		NetworkHandlerServer.sendUpdatePackets();
		PlayerUtils.resendCommandTrees();
	}
	public static void reloadEnd() {
		DatapackManager.onReloadEnd();
	}

	public static boolean changeSeasonTo(String changeTo) {
		if (changeTo.equalsIgnoreCase("wildlife")) {
			if (!DependencyManager.checkWildLifeDependencies()) return false;
		}

		config.setProperty("currentSeries", changeTo);
		currentSeason.resetAllPlayerLives();
		currentSession.sessionEnd();
		Main.parseSeason(changeTo);
		currentSeason.initialize();
		reloadStart();
		for (ServerPlayerEntity player : PlayerUtils.getAllPlayers()) {
			currentSeason.onPlayerJoin(player);
			currentSeason.onPlayerFinishJoining(player);
			NetworkHandlerServer.tryKickFailedHandshake(player);
			NetworkHandlerServer.sendStringPacket(player, "season_info", Seasons.getStringNameFromSeason(currentSeason.getSeason()));
			NetworkHandlerServer.sendLongPacket(player, "session_timer", -1);
		}
		SessionTranscript.resetStats();
		return true;
	}
}