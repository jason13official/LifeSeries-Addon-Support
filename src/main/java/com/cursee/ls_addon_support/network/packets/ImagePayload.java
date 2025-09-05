package com.cursee.ls_addon_support.network.packets;

import com.cursee.ls_addon_support.LSAddonSupport;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record ImagePayload(String name, int index, int maxIndex, byte[] bytes) implements
    CustomPayload {

  public static final Id<ImagePayload> ID = new Id<>(Identifier.of(LSAddonSupport.MOD_ID, "image"));
  public static final PacketCodec<RegistryByteBuf, ImagePayload> CODEC = PacketCodec.tuple(
      PacketCodecs.STRING, ImagePayload::name,
      PacketCodecs.INTEGER, ImagePayload::index,
      PacketCodecs.INTEGER, ImagePayload::maxIndex,
      PacketCodecs.BYTE_ARRAY, ImagePayload::bytes,
      ImagePayload::new
  );

  @Override
  public Id<? extends CustomPayload> getId() {
    return ID;
  }
}