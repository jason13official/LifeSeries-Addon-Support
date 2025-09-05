package com.cursee.ls_addon_support.config.entries;

import com.cursee.ls_addon_support.network.packets.ConfigPayload;

public class BooleanObject extends ConfigObject {

  public boolean booleanValue;
  public boolean defaultValue;
  public boolean startingValue;

  public BooleanObject(ConfigPayload payload, boolean booleanValue, boolean defaultValue) {
    super(payload);
    this.booleanValue = booleanValue;
    this.defaultValue = defaultValue;
    this.startingValue = booleanValue;
  }
}
