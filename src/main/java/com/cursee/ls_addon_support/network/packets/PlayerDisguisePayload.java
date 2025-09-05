package com.cursee.ls_addon_support.network.packets;

import com.cursee.ls_addon_support.LSAddonSupport;
import com.cursee.ls_addon_support.utils.enums.PacketNames;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record PlayerDisguisePayload(String name, String hiddenUUID, String hiddenName,
                                    String shownUUID, String shownName) implements CustomPayload {

  public static final Id<PlayerDisguisePayload> ID = new Id<>(
      Identifier.of(LSAddonSupport.MOD_ID, PacketNames.PLAYER_DISGUISE.getName()));
  public static final PacketCodec<RegistryByteBuf, PlayerDisguisePayload> CODEC = PacketCodec.tuple(
      PacketCodecs.STRING, PlayerDisguisePayload::name,
      PacketCodecs.STRING, PlayerDisguisePayload::hiddenUUID,
      PacketCodecs.STRING, PlayerDisguisePayload::hiddenName,
      PacketCodecs.STRING, PlayerDisguisePayload::shownUUID,
      PacketCodecs.STRING, PlayerDisguisePayload::shownName,
      PlayerDisguisePayload::new
  );

  @Override
  public Id<? extends CustomPayload> getId() {
    return ID;
  }
}