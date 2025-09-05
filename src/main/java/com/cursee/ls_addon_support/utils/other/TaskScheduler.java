package com.cursee.ls_addon_support.utils.other;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class TaskScheduler {

  private static final List<Task> tasks = new ArrayList<>();
  private static final List<Task> newTasks = new ArrayList<>();

  public static void scheduleTask(int tickNumber, Runnable goal) {
    Task task = new Task(tickNumber, goal);
    newTasks.add(task);
  }

  public static void registerTickHandler() {
    ServerTickEvents.END_SERVER_TICK.register(server -> {
      try {
        Iterator<Task> iterator = tasks.iterator();

        while (iterator.hasNext()) {
          Task task = iterator.next();
          task.tickCount--;

          if (task.tickCount <= 0) {
            task.goal.run();
            iterator.remove();
          }
        }

        tasks.addAll(newTasks);
        newTasks.clear();
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
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
