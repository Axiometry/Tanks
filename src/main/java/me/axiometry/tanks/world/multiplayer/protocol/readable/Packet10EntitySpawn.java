package me.axiometry.tanks.world.multiplayer.protocol.readable;

import me.axiometry.tanks.entity.*;
import me.axiometry.tanks.entity.multiplayer.*;
import me.axiometry.tanks.server.io.protocol.ByteStream;
import me.axiometry.tanks.world.multiplayer.NetworkManager;
import me.axiometry.tanks.world.multiplayer.protocol.*;

import java.io.IOException;

public class Packet10EntitySpawn extends AbstractPacket implements
		ReadablePacket {
	protected short netID;
	protected int id;
	protected double x, y, z, rotation;
	protected Metadata metadata;

	@Override
	public byte getID() {
		return 10;
	}

	@Override
	public void readData(ByteStream stream) throws IOException {
		netID = readShort(stream);
		id = readInt(stream);
		x = readInt(stream) / 32D;
		y = readInt(stream) / 32D;
		rotation = readShort(stream) / 32D;
		metadata = new MetadataImpl();
		readMetadata(metadata, stream);
	}

	@Override
	public void processData(NetworkManager manager) {
		MultiPlayerEntity entity = EntityCache.getEntityCache()
				.newEntity(netID);
		entity.setID(id);
		entity.setX(x);
		entity.setY(y);
		entity.setRotation(rotation);
		entity.updateMetadata(metadata);
		game.getWorld().spawnEntity(entity);
		entity.updatePreNetwork();
		entity.updateNetwork();
	}
}
