package com.cursee.ls_addon_support.config.entries;

import com.cursee.ls_addon_support.network.packets.ConfigPayload;

public class IntegerObject extends ConfigObject {

  public int integerValue;
  public int defaultValue;
  public int startingValue;

  public IntegerObject(ConfigPayload payload, int integerValue, int defaultValue) {
    super(payload);
    this.integerValue = integerValue;
    this.defaultValue = defaultValue;
    this.startingValue = integerValue;
  }
}