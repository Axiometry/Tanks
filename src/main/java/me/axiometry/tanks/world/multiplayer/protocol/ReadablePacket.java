package me.axiometry.tanks.world.multiplayer.protocol;

import me.axiometry.tanks.server.io.protocol.ByteStream;
import me.axiometry.tanks.world.multiplayer.NetworkManager;

import java.io.IOException;

public interface ReadablePacket extends Packet {
	public void readData(ByteStream stream) throws IOException;

	public void processData(NetworkManager manager);
}
