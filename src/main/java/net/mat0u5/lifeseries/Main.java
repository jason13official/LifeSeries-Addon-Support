package net.mat0u5.lifeseries;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.loader.api.FabricLoader;
import net.mat0u5.lifeseries.resources.config.ConfigManager;
import net.mat0u5.lifeseries.resources.config.MainConfig;
import net.mat0u5.lifeseries.resources.datapack.DatapackManager;
import net.mat0u5.lifeseries.utils.UpdateChecker;
import net.mat0u5.lifeseries.dependencies.DependencyManager;
import net.mat0u5.lifeseries.dependencies.PolymerDependency;
import net.mat0u5.lifeseries.events.Events;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.series.*;
import net.mat0u5.lifeseries.series.aprilfools.simplelife.SimpleLife;
import net.mat0u5.lifeseries.series.secretlife.SecretLife;
import net.mat0u5.lifeseries.series.secretlife.TaskManager;
import net.mat0u5.lifeseries.series.unassigned.UnassignedSeries;
import net.mat0u5.lifeseries.series.doublelife.DoubleLife;
import net.mat0u5.lifeseries.series.lastlife.LastLife;
import net.mat0u5.lifeseries.series.limitedlife.LimitedLife;
import net.mat0u5.lifeseries.series.thirdlife.ThirdLife;
import net.mat0u5.lifeseries.series.wildlife.WildLife;
import net.mat0u5.lifeseries.registries.ModRegistries;
import net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.snails.SnailSkinsServer;
import net.mat0u5.lifeseries.utils.OtherUtils;
import net.mat0u5.lifeseries.utils.PlayerUtils;
import net.mat0u5.lifeseries.utils.TextUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

public class Main implements ModInitializer {
	public static final String MOD_VERSION = "dev-1.3.4.8";
	public static final String MOD_ID = "lifeseries";
	public static final String MAJOR_UPDATE_URL = "https://api.github.com/repos/Mat0u5/LifeSeries/releases/latest";
	public static final String ALL_UPDATES_URL = "https://api.github.com/repos/Mat0u5/LifeSeries/releases";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final boolean DEBUG = false;
	private static ConfigManager config;

	@Nullable
	public static MinecraftServer server;
	public static Series currentSeries;
	public static Session currentSession;
	public static Blacklist blacklist;
	public static ConfigManager seriesConfig;
	public static final List<String> ALLOWED_SERIES_NAMES = SeriesList.getImplementedSeriesNames();

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing Life Series...");
		ConfigManager.moveOldMainFileIfExists();
		SnailSkinsServer.createConfig();

		if (DependencyManager.polymerLoaded()) {
			PolymerDependency.onInitialize();
		}

		config = new MainConfig();
		String series = config.getOrCreateProperty("currentSeries", "unassigned");

		parseSeries(series);
		ConfigManager.createConfigs();

		ModRegistries.registerModStuff();
		UpdateChecker.checkForMajorUpdates();

		NetworkHandlerServer.registerPackets();
		NetworkHandlerServer.registerServerReceiver();
	}


	public static boolean isClient() {
		return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
	}

	public static boolean isLogicalSide() {
		if (!isClient()) return true;
		return MainClient.isRunningIntegratedServer();
	}

	public static boolean isClientPlayer(UUID uuid) {
		if (!isClient()) return false;
		return MainClient.isClientPlayer(uuid);
	}

	public static void parseSeries(String series) {
		if (!ALLOWED_SERIES_NAMES.contains(series)) {
			currentSeries = new UnassignedSeries();
		}
		if (series.equalsIgnoreCase("thirdlife")) {
			currentSeries = new ThirdLife();
		}
		if (series.equalsIgnoreCase("lastlife")) {
			currentSeries = new LastLife();
		}
		if (series.equalsIgnoreCase("doublelife")) {
			currentSeries = new DoubleLife();
		}
		if (series.equalsIgnoreCase("limitedlife")) {
			currentSeries = new LimitedLife();
		}
		if (series.equalsIgnoreCase("secretlife")) {
			currentSeries = new SecretLife();
		}
		if (series.equalsIgnoreCase("wildlife")) {
			if (DependencyManager.polymerLoaded() && DependencyManager.wildLifeModsLoaded()) {
				currentSeries = new WildLife();
			}
			else {
				currentSeries = new UnassignedSeries();
				changeSeriesTo("unassigned");
			}
		}
		if (series.equalsIgnoreCase("simplelife")) {
			currentSeries = new SimpleLife();
		}
		currentSession = currentSeries;
		seriesConfig = currentSeries.getConfig();
		blacklist = currentSeries.createBlacklist();
	}

	public static void reloadStart() {
		if (Events.skipNextTickReload) return;
		if (!isLogicalSide()) return;
		if (currentSeries.getSeries() == SeriesList.SECRET_LIFE) {
			TaskManager.initialize();
		}
		if (currentSeries.getSeries() == SeriesList.DOUBLE_LIFE) {
			((DoubleLife) currentSeries).loadSoulmates();
		}
		seriesConfig.loadProperties();
		blacklist.reloadBlacklist();
		currentSeries.reload();
		NetworkHandlerServer.sendUpdatePackets();
		SnailSkinsServer.sendStoredImages();
		PlayerUtils.resendCommandTrees();
		DatapackManager.onReloadStart();
	}
	public static void reloadEnd() {
		DatapackManager.onReloadEnd();
	}

	public static boolean changeSeriesTo(String changeTo) {
		if (changeTo.equalsIgnoreCase("wildlife")) {
			boolean success = true;
			if (!DependencyManager.polymerLoaded()) {
				success = false;
				OtherUtils.broadcastMessage(
						Text.literal("§cYou must install the ").append(
								Text.literal("Polymer mod")
										.styled(style -> style
												.withColor(Formatting.BLUE)
												.withClickEvent(TextUtils.openURLClickEvent("https://modrinth.com/mod/polymer"))
												.withUnderline(true)
										)
						).append(Text.of(" §cto play Wild Life."))
				);
			}
			if (!DependencyManager.blockbenchImportLibraryLoaded()) {
				success = false;
				OtherUtils.broadcastMessage(
						Text.literal("§cYou must install the ").append(
								Text.literal("Blockbench Import Library mod")
										.styled(style -> style
												.withColor(Formatting.BLUE)
												.withClickEvent(TextUtils.openURLClickEvent("https://modrinth.com/mod/blockbench-import-library"))
												.withUnderline(true)
										)
						).append(Text.of(" §cto play Wild Life."))
				);
			}
			if (!DependencyManager.cardinalComponentsLoaded()) {
				success = false;
				OtherUtils.broadcastMessage(
						Text.literal("§cYou must install the ").append(
								Text.literal("Cardinal Components API mod")
										.styled(style -> style
												.withColor(Formatting.BLUE)
												.withClickEvent(TextUtils.openURLClickEvent("https://modrinth.com/mod/cardinal-components-api"))
												.withUnderline(true)
										)
						).append(Text.of(" §cto play Wild Life."))
				);
			}
			if (!success) return false;
		}

		config.setProperty("currentSeries", changeTo);
		currentSeries.resetAllPlayerLives();
		Main.parseSeries(changeTo);
		currentSeries.initialize();
		reloadStart();
		for (ServerPlayerEntity player : PlayerUtils.getAllPlayers()) {
			currentSeries.onPlayerJoin(player);
			currentSeries.onPlayerFinishJoining(player);
			NetworkHandlerServer.tryKickFailedHandshake(player);
			NetworkHandlerServer.sendStringPacket(player, "series_info", SeriesList.getStringNameFromSeries(currentSeries.getSeries()));
			NetworkHandlerServer.sendLongPacket(player, "session_timer", -1);
		}
		Stats.resetStats();
		return true;
	}
}