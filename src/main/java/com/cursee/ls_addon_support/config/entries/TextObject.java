package com.cursee.ls_addon_support.config.entries;

import com.cursee.ls_addon_support.network.packets.ConfigPayload;

public class TextObject extends ConfigObject {

  public String text;
  public boolean clickable;

  public TextObject(ConfigPayload payload, String text, boolean clickable) {
    super(payload);
    this.text = text;
    this.clickable = clickable;
  }
}