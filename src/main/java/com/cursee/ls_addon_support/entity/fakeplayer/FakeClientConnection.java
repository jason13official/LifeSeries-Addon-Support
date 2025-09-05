package com.cursee.ls_addon_support.entity.fakeplayer;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.state.NetworkState;
import net.minecraft.network.listener.PacketListener;

/*
 * This file includes code from the Fabric Carpet project: https://github.com/gnembon/fabric-carpet
 *
 * Used and modified under the MIT License.
 */
public class FakeClientConnection extends ClientConnection {

  public FakeClientConnection(NetworkSide side) {
    super(side);
  }

  @Override
  public void tryDisableAutoRead() {
  }

  @Override
  public void handleDisconnection() {
  }

  @Override
  public void setInitialPacketListener(PacketListener packetListener) {
  }

  @Override
  public <T extends PacketListener> void transitionInbound(NetworkState<T> protocolInfo,
      T packetListener) {
  }
}