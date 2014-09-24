package me.axiometry.tanks.world.multiplayer.protocol.readable;

import me.axiometry.tanks.entity.Entity;
import me.axiometry.tanks.server.io.protocol.ByteStream;
import me.axiometry.tanks.world.multiplayer.NetworkManager;
import me.axiometry.tanks.world.multiplayer.protocol.*;

import java.io.IOException;

public class Packet15EntityDespawn extends AbstractPacket implements
		ReadablePacket {
	private int id;

	@Override
	public byte getID() {
		return 15;
	}

	@Override
	public void readData(ByteStream stream) throws IOException {
		id = readInt(stream);
	}

	@Override
	public void processData(NetworkManager manager) {
		Entity entity = game.getWorld().getEntityById(id);
		if(entity == null)
			return;
		entity.kill();
	}

}
