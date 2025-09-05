package com.cursee.ls_addon_support.seasons.session;

public enum SessionStatus {
  NOT_STARTED("Not Started"),
  STARTED("Started"),
  PAUSED("Paused"),
  FINISHED("Finished"),
  UNASSIGNED("Unassigned");

  private final String name;

  SessionStatus(String name) {
    this.name = name;
  }

  public static SessionStatus getSessionName(String name) {
    for (SessionStatus status : SessionStatus.values()) {
      if (status.getName().equalsIgnoreCase(name)) {
        return status;
      }
    }
    return UNASSIGNED;
  }

  public String getName() {
    return name;
  }
}
