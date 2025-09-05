package com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.snails;

import static com.cursee.ls_addon_support.LSAddonSupport.currentSeason;

import com.cursee.ls_addon_support.LSAddonSupport;
import com.cursee.ls_addon_support.network.NetworkHandlerServer;
import com.cursee.ls_addon_support.network.packets.ImagePayload;
import com.cursee.ls_addon_support.resources.ResourceHandler;
import com.cursee.ls_addon_support.seasons.season.Seasons;
import com.cursee.ls_addon_support.utils.enums.PacketNames;
import com.cursee.ls_addon_support.utils.player.PlayerUtils;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import net.minecraft.server.network.ServerPlayerEntity;

public class SnailSkinsServer {

  public static Map<String, Integer> indexedSkins = new HashMap<>();
  public static int currentIndex = 0;

  public static void sendImageToClient(ServerPlayerEntity player, String name, int index,
      int maxIndex, Path imagePath) {
    try {
      BufferedImage image = ImageIO.read(Files.newInputStream(imagePath));

      if (image.getWidth() != 32 || image.getHeight() != 32) {
        LSAddonSupport.LOGGER.error("Image must be 32x32 pixels");
        return;
      }

      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ImageIO.write(image, "PNG", baos);
      byte[] imageBytes = baos.toByteArray();

      ImagePayload payload = new ImagePayload(name, index, maxIndex, imageBytes);

      NetworkHandlerServer.sendImagePacket(player, payload);

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void sendStoredImages(List<ServerPlayerEntity> players) {
      if (currentSeason.getSeason() != Seasons.WILD_LIFE) {
          return;
      }
    File folder = new File("./config/lifeseries/wildlife/snailskins/");
    if (!folder.exists()) {
      if (!folder.mkdirs()) {
        LSAddonSupport.LOGGER.error("Failed to create folder {}", folder);
        return;
      }
    }
    File[] files = folder.listFiles();
      if (files == null) {
          return;
      }
    int maxIndex = 0;
    for (File file : files) {
        if (!file.isFile()) {
            continue;
        }
      String name = file.getName().toLowerCase();
        if (name.equalsIgnoreCase("example.png")) {
            continue;
        }
        if (!name.endsWith(".png")) {
            continue;
        }
      String replacedName = name.toLowerCase().replaceAll(".png", "");
      if (!indexedSkins.containsKey(replacedName)) {
        indexedSkins.put(replacedName, currentIndex);
        currentIndex++;
      }
      int imageIndex = indexedSkins.get(replacedName);
      maxIndex = Math.max(maxIndex, imageIndex);
    }
    for (File file : files) {
        if (!file.isFile()) {
            continue;
        }
      String name = file.getName().toLowerCase();
        if (name.equalsIgnoreCase("example.png")) {
            continue;
        }
        if (!name.endsWith(".png")) {
            continue;
        }
      String replacedName = name.toLowerCase().replaceAll(".png", "");
      int imageIndex = indexedSkins.get(replacedName);
      for (ServerPlayerEntity player : players) {
        sendImageToClient(player, PacketNames.SNAIL_SKIN.getName(), imageIndex, maxIndex,
            file.toPath());
      }
    }
  }

  public static List<String> getAllSkins() {
    List<String> result = new ArrayList<>();
    File folder = new File("./config/lifeseries/wildlife/snailskins/");
    File[] files = folder.listFiles();
      if (files == null) {
          return result;
      }
    for (File file : files) {
        if (!file.isFile()) {
            continue;
        }
      String name = file.getName().toLowerCase();
        if (name.equalsIgnoreCase("example.png")) {
            continue;
        }
        if (!name.endsWith(".png")) {
            continue;
        }
      String replacedName = name.replaceAll(".png", "");
      result.add(replacedName);
    }
    return result;
  }

  public static void sendStoredImages() {
    sendStoredImages(PlayerUtils.getAllPlayers());
  }

  public static void createConfig() {
    File folder = new File("./config/lifeseries/wildlife/snailskins/");
    if (!folder.exists()) {
      if (!folder.mkdirs()) {
        LSAddonSupport.LOGGER.error("Failed to create folder {}", folder);
        return;
      }
    }
    ResourceHandler handler = new ResourceHandler();

    Path modelResult = new File("./config/lifeseries/wildlife/snailskins/snail.bbmodel").toPath();
    handler.copyBundledSingleFile("/model/" + LSAddonSupport.MOD_ID + "/snail.bbmodel",
        modelResult);

    Path textureResult = new File("./config/lifeseries/wildlife/snailskins/example.png").toPath();
    handler.copyBundledSingleFile("/model/" + LSAddonSupport.MOD_ID + "/texture/example.png",
        textureResult);
  }
}
