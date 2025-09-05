package com.cursee.ls_addon_support.config.entries;

import com.cursee.ls_addon_support.network.packets.ConfigPayload;

public class DoubleObject extends ConfigObject {

  public double doubleValue;
  public double defaultValue;
  public double startingValue;

  public DoubleObject(ConfigPayload payload, double doubleValue, double defaultValue) {
    super(payload);
    this.doubleValue = doubleValue;
    this.defaultValue = defaultValue;
    this.startingValue = doubleValue;
  }
}
