package com.cursee.ls_addon_support.seasons.season.secretlife;

import com.cursee.ls_addon_support.config.StringListConfig;
import java.util.ArrayList;
import java.util.List;

public class SecretLifeUsedTasks {

  public static void deleteAllTasks(StringListConfig config, List<String> tasks) {
      if (config == null) {
          return;
      }

    List<String> allTasks = getUsedTasks(config);
    for (String task : tasks) {
      allTasks.remove(task);
    }

    config.save(allTasks);
  }

  public static void addUsedTask(StringListConfig config, String task) {
      if (config == null) {
          return;
      }

    List<String> allTasks = getUsedTasks(config);
      if (!allTasks.contains(task)) {
          allTasks.add(task);
      }

    config.save(allTasks);
  }

  public static List<String> getUsedTasks(StringListConfig config) {
      if (config == null) {
          return new ArrayList<>();
      }

    return config.load();
  }
}
