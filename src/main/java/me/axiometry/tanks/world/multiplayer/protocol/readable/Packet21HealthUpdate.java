package me.axiometry.tanks.world.multiplayer.protocol.readable;

import me.axiometry.tanks.entity.LivingEntity;
import me.axiometry.tanks.server.io.protocol.ByteStream;
import me.axiometry.tanks.world.multiplayer.NetworkManager;
import me.axiometry.tanks.world.multiplayer.protocol.*;

import java.io.IOException;

public class Packet21HealthUpdate extends AbstractPacket implements
		ReadablePacket {
	private int id, health;

	@Override
	public byte getID() {
		return 21;
	}

	@Override
	public void readData(ByteStream stream) throws IOException {
		id = readInt(stream);
		health = readInt(stream);
	}

	@Override
	public void processData(NetworkManager manager) {
		LivingEntity entity = (LivingEntity) game.getWorld().getEntityById(id);
		if(entity == null)
			return;
		entity.setHealth(health);
	}
}
