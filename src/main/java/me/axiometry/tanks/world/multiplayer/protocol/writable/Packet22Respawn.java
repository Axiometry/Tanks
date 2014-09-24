package me.axiometry.tanks.world.multiplayer.protocol.writable;

import me.axiometry.tanks.server.io.protocol.ByteStream;
import me.axiometry.tanks.world.multiplayer.protocol.*;

import java.io.IOException;

public class Packet22Respawn extends AbstractPacket implements WritablePacket {
	public Packet22Respawn() {
	}

	@Override
	public byte getID() {
		return 22;
	}

	@Override
	public void writeData(ByteStream stream) throws IOException {
	}
}
