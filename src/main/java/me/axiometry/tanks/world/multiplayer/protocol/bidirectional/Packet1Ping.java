package me.axiometry.tanks.world.multiplayer.protocol.bidirectional;

import me.axiometry.tanks.server.io.protocol.ByteStream;
import me.axiometry.tanks.world.multiplayer.NetworkManager;
import me.axiometry.tanks.world.multiplayer.protocol.*;

import java.io.IOException;
import java.util.Random;

public class Packet1Ping extends AbstractPacket implements ReadablePacket,
		WritablePacket {
	private static final Random random = new Random();

	@Override
	public byte getID() {
		return 1;
	}

	@Override
	public void writeData(ByteStream stream) throws IOException {
		writeInt(random.nextInt(), stream);
	}

	@Override
	public void readData(ByteStream stream) throws IOException {
		readInt(stream);
	}

	@Override
	public void processData(NetworkManager manager) {
		System.out.println("Pong!");
	}

}
