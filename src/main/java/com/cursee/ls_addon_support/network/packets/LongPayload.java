package com.cursee.ls_addon_support.network.packets;

import com.cursee.ls_addon_support.LSAddonSupport;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record LongPayload(String name, long number) implements CustomPayload {

  public static final Id<LongPayload> ID = new Id<>(Identifier.of(LSAddonSupport.MOD_ID, "long"));
  public static final PacketCodec<RegistryByteBuf, LongPayload> CODEC = PacketCodec.tuple(
      PacketCodecs.STRING, LongPayload::name,
      PacketCodecs.VAR_LONG, LongPayload::number,
      LongPayload::new
  );

  @Override
  public Id<? extends CustomPayload> getId() {
    return ID;
  }
}