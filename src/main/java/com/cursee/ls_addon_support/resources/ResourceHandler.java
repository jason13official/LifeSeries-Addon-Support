package com.cursee.ls_addon_support.resources;

import com.cursee.ls_addon_support.LSAddonSupport;
import com.cursee.ls_addon_support.utils.other.TextUtils;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class ResourceHandler {

  public void copyBundledSingleFile(String resourcePath, Path targetFile) {
    try {
      Files.createDirectories(targetFile.getParent());

      URL resourceUrl = getClass().getResource(resourcePath);

      if (resourceUrl == null) {
        LSAddonSupport.LOGGER.error("File not found: " + resourcePath);
        return;
      }

      if (resourceUrl.getProtocol().equals("file")) {
        handleSingleFileNormal(targetFile, resourceUrl);
      } else if (resourceUrl.getProtocol().equals("jar")) {
        handleSingleFileJar(targetFile, resourcePath);
      } else {
        LSAddonSupport.LOGGER.error("Unsupported resource protocol: " + resourceUrl.getProtocol());
      }
    } catch (Exception e) {
      LSAddonSupport.LOGGER.error("Error copying bundled file: " + resourcePath, e);
    }
  }

  private void handleSingleFileNormal(Path targetFile, URL resourceUrl) {
    try {
      Path sourcePath = Paths.get(resourceUrl.toURI());

      if (Files.isRegularFile(sourcePath)) {
        Files.copy(sourcePath, targetFile, StandardCopyOption.REPLACE_EXISTING);
        LSAddonSupport.LOGGER.info(
            TextUtils.formatString("Copied file: {} -> {}", sourcePath, targetFile));
      } else {
        LSAddonSupport.LOGGER.error("Source is not a regular file: " + sourcePath);
      }
    } catch (Exception e) {
      LSAddonSupport.LOGGER.error("Error copying bundled file.", e);
    }
  }

  private void handleSingleFileJar(Path targetFile, String resourcePath) {
    try {
      try (InputStream in = getClass().getResourceAsStream(resourcePath)) {
        if (in == null) {
          LSAddonSupport.LOGGER.error("Could not find resource: " + resourcePath);
          return;
        }

        Files.copy(in, targetFile, StandardCopyOption.REPLACE_EXISTING);
        LSAddonSupport.LOGGER.info(
            TextUtils.formatString("Copied file from JAR: {} -> {}", resourcePath, targetFile));
      }
    } catch (Exception e) {
      LSAddonSupport.LOGGER.error("Error copying file from JAR: " + resourcePath, e);
    }
  }
}
