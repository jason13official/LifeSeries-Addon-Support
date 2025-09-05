package com.cursee.ls_addon_support.utils.enums;

public enum MColor {
  BLACK("§0"),
  DARK_BLUE("§1"),
  DARK_GREEN("§2"),
  DARK_AQUA("§3"),
  DARK_RED("§4"),
  DARK_PURPLE("§5"),
  GOLD("§6"),
  GRAY("§7"),
  DARK_GRAY("§8"),
  BLUE("§9"),
  GREEN("§a"),
  AQUA("§b"),
  RED("§c"),
  LIGHT_PURPLE("§d"),
  YELLOW("§e"),
  WHITE("§f"),
  OBFUSCATED("§k"),
  BOLD("§l"),
  STRIKETHROUGH("§m"),
  UNDERLINE("§n"),
  ITALIC("§o"),
  RESET("§r");

  private final String code;

  MColor(String code) {
    this.code = code;
  }

  public String getCode() {
    return code;
  }
}
