package me.axiometry.tanks.world.multiplayer.protocol.writable;

import me.axiometry.tanks.server.io.protocol.ByteStream;
import me.axiometry.tanks.world.multiplayer.protocol.*;

import java.io.IOException;

public class Packet20Fire extends AbstractPacket implements WritablePacket {
	public enum WeaponType {
		BULLET,
		CHARGE
	}

	private final WeaponType type;
	private final double rotation;

	public Packet20Fire(WeaponType type, double rotation) {
		this.type = type;
		this.rotation = rotation;
	}

	@Override
	public byte getID() {
		return 20;
	}

	@Override
	public void writeData(ByteStream stream) throws IOException {
		stream.write((byte) type.ordinal());
		writeDouble(rotation, stream);
	}

	public WeaponType getType() {
		return type;
	}
}
