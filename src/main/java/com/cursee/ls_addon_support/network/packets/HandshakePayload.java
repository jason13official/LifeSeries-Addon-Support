package com.cursee.ls_addon_support.network.packets;

import com.cursee.ls_addon_support.LSAddonSupport;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record HandshakePayload(String modVersionStr, int modVersion, String compatibilityStr,
                               int compatibility) implements CustomPayload {

  public static final Id<HandshakePayload> ID = new Id<>(
      Identifier.of(LSAddonSupport.MOD_ID, "handshake"));
  public static final PacketCodec<RegistryByteBuf, HandshakePayload> CODEC = PacketCodec.tuple(
      PacketCodecs.STRING, HandshakePayload::modVersionStr,
      PacketCodecs.INTEGER, HandshakePayload::modVersion,
      PacketCodecs.STRING, HandshakePayload::compatibilityStr,
      PacketCodecs.INTEGER, HandshakePayload::compatibility,
      HandshakePayload::new
  );

  @Override
  public Id<? extends CustomPayload> getId() {
    return ID;
  }
}