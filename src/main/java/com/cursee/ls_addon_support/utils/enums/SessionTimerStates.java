package com.cursee.ls_addon_support.utils.enums;

public enum SessionTimerStates {
  ENDED(-3),
  PAUSED(-2),
  NOT_STARTED(-1),
  OFF(0);

  private final int value;

  SessionTimerStates(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }
}
