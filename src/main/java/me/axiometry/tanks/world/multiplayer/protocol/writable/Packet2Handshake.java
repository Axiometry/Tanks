package me.axiometry.tanks.world.multiplayer.protocol.writable;

import me.axiometry.tanks.server.io.protocol.ByteStream;
import me.axiometry.tanks.world.multiplayer.protocol.*;

import java.io.IOException;

public class Packet2Handshake extends AbstractPacket implements WritablePacket {
	private String username;

	public Packet2Handshake(String username) {
		this.username = username;
	}

	@Override
	public byte getID() {
		return 2;
	}

	@Override
	public void writeData(ByteStream stream) throws IOException {
		writeString(username, stream);
	}

	public String getUsername() {
		return username;
	}
}
