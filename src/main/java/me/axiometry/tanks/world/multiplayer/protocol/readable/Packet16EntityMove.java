package me.axiometry.tanks.world.multiplayer.protocol.readable;

import me.axiometry.tanks.entity.multiplayer.MultiPlayerEntity;
import me.axiometry.tanks.server.io.protocol.ByteStream;
import me.axiometry.tanks.world.multiplayer.NetworkManager;
import me.axiometry.tanks.world.multiplayer.protocol.*;

import java.io.IOException;

public class Packet16EntityMove extends AbstractPacket implements
		ReadablePacket {
	protected int id;
	protected double x, y, speedX, speedY, rotation;

	@Override
	public byte getID() {
		return 16;
	}

	@Override
	public void readData(ByteStream stream) throws IOException {
		id = readInt(stream);
		x = readInt(stream) / 32D;
		y = readInt(stream) / 32D;
		speedX = readShort(stream) / 32D;
		speedY = readShort(stream) / 32D;
		rotation = readShort(stream) / 32D;
	}

	@Override
	public void processData(NetworkManager manager) {
		MultiPlayerEntity entity = (MultiPlayerEntity) game.getWorld()
				.getEntityById(id);
		if(entity == null)
			return;
		entity.updatePreNetwork();
		entity.setX(x);
		entity.setY(y);
		entity.setSpeedX(speedX);
		entity.setSpeedY(speedY);
		entity.setRotation(rotation);
		entity.updateNetwork();
	}

}
