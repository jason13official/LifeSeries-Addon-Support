package com.cursee.ls_addon_support.config.entries;

import com.cursee.ls_addon_support.network.packets.ConfigPayload;
import com.cursee.ls_addon_support.utils.enums.ConfigTypes;
import java.util.List;

public class ConfigObject {

  public ConfigTypes configType;
  public String id;
  public String name;
  public String description;
  public List<String> args;
  public boolean modified = false;
  private final int index;

  public ConfigObject(ConfigPayload payload) {
    configType = ConfigTypes.getFromString(payload.configType());
    index = payload.index();
    id = payload.id();
    name = payload.name();
    description = payload.description();
    args = payload.args();
  }

  public String getGroupInfo() {
    if (args.size() < 3) {
      return "";
    }
    return args.get(2);
  }
}
