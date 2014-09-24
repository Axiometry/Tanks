package me.axiometry.tanks.world.multiplayer.protocol;

import me.axiometry.tanks.server.io.protocol.ByteStream;

import java.io.IOException;

public interface WritablePacket extends Packet {
	public void writeData(ByteStream stream) throws IOException;
}
