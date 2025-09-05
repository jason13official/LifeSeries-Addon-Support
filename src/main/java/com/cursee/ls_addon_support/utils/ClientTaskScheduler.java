package com.cursee.ls_addon_support.utils;

import com.cursee.ls_addon_support.LSAddonSupport;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ClientTaskScheduler {

  private static final List<Task> tasks = new ArrayList<>();
  private static final List<Task> newTasks = new ArrayList<>();

  public static void scheduleTask(int tickNumber, Runnable goal) {
    Task task = new Task(tickNumber, goal);
    newTasks.add(task);
  }

  public static void onClientTick() {
    try {
      Iterator<Task> iterator = tasks.iterator();

      while (iterator.hasNext()) {
        Task task = iterator.next();
        task.tickCount--;

        if (task.tickCount <= 0) {
          try {
            //Inner try-catch to prevent errors from preventing the task from being removed
            task.goal.run();
          } catch (Exception e) {
            LSAddonSupport.LOGGER.error(e.getMessage());
          }
          iterator.remove();
        }
      }

      tasks.addAll(newTasks);
      newTasks.clear();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static class Task {

    private final Runnable goal;
    private int tickCount;

    public Task(int tickCount, Runnable goal) {
      this.tickCount = tickCount;
      this.goal = goal;
    }
  }
}
