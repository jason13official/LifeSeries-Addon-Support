package com.cursee.ls_addon_support.seasons.season.unassigned;

import static com.cursee.ls_addon_support.LSAddonSupport.currentSeason;

import com.cursee.ls_addon_support.config.ConfigFileEntry;
import com.cursee.ls_addon_support.config.ConfigManager;
import com.cursee.ls_addon_support.network.NetworkHandlerServer;
import com.cursee.ls_addon_support.seasons.season.Season;
import com.cursee.ls_addon_support.seasons.season.Seasons;
import com.cursee.ls_addon_support.utils.enums.PacketNames;
import com.cursee.ls_addon_support.utils.other.TaskScheduler;
import com.cursee.ls_addon_support.utils.other.TextUtils;
import com.cursee.ls_addon_support.utils.player.PlayerUtils;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class UnassignedSeason extends Season {

  @Override
  public Seasons getSeason() {
    return Seasons.UNASSIGNED;
  }

  @Override
  public ConfigManager createConfig() {
    return new ConfigManager(null, null) {
      @Override
      public void instantiateProperties() {
      }

      @Override
      protected List<ConfigFileEntry<?>> getDefaultConfigEntries() {
        return new ArrayList<>(List.of());
      }
    };
  }

  @Override
  public void onPlayerJoin(ServerPlayerEntity player) {
    TaskScheduler.scheduleTask(100, this::broadcastNotice);
  }

  @Override
  public void onPlayerFinishJoining(ServerPlayerEntity player) {
    super.onPlayerFinishJoining(player);
    NetworkHandlerServer.sendStringPacket(player, PacketNames.SELECT_SEASON, "");
  }

  @Override
  public void initialize() {
    super.initialize();
    broadcastNotice();
  }

  @Override
  public String getAdminCommands() {
    return "";
  }

  @Override
  public String getNonAdminCommands() {
    return "";
  }

  @Override
  public Integer getDefaultLives() {
    return null;
  }

  public void broadcastNotice() {
      if (currentSeason.getSeason() != Seasons.UNASSIGNED) {
          return;
      }
    PlayerUtils.broadcastMessage(
        Text.literal("[LifeSeries] You must select a season with ").formatted(Formatting.RED)
            .append(Text.literal("'/lifeseries setSeries <series>'").formatted(Formatting.GRAY)),
        120);
    PlayerUtils.broadcastMessage(
        Text.literal("You must have §noperator permissions§r to use most commands in this mod.")
            .formatted(Formatting.RED), 120);
    Text text = Text.literal("§7Click ").append(
        Text.literal("here")
            .styled(style -> style
                .withColor(Formatting.BLUE)
                .withClickEvent(TextUtils.openURLClickEvent("https://discord.gg/QWJxfb4zQZ"))
                .withUnderline(true)
            )).append(Text.of(
        "§7 to join the mod development discord if you have any questions, issues, requests, or if you just want to hang out :)"));
    PlayerUtils.broadcastMessage(text, 120);
  }
}
