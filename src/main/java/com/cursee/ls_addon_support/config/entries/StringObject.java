package com.cursee.ls_addon_support.config.entries;

import com.cursee.ls_addon_support.network.packets.ConfigPayload;

public class StringObject extends ConfigObject {

  public String stringValue;
  public String defaultValue;
  public String startingValue;

  public StringObject(ConfigPayload payload, String stringValue, String defaultValue) {
    super(payload);
    this.stringValue = stringValue;
    this.defaultValue = defaultValue;
    this.startingValue = stringValue;
  }
}
