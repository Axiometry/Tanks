package me.axiometry.tanks.world.multiplayer.protocol.readable;

import me.axiometry.tanks.entity.*;
import me.axiometry.tanks.entity.multiplayer.MultiPlayerEntity;
import me.axiometry.tanks.server.io.protocol.ByteStream;
import me.axiometry.tanks.world.multiplayer.NetworkManager;
import me.axiometry.tanks.world.multiplayer.protocol.*;

import java.io.IOException;

public class Packet14UpdateMetadata extends AbstractPacket implements
		ReadablePacket {
	protected int id;
	protected Metadata metadata;

	@Override
	public byte getID() {
		return 14;
	}

	@Override
	public void readData(ByteStream stream) throws IOException {
		id = readInt(stream);
		metadata = new MetadataImpl();
		readMetadata(metadata, stream);
	}

	@Override
	public void processData(NetworkManager manager) {
		Entity entity = game.getWorld().getEntityById(id);
		entity.updateMetadata(metadata);
		if(entity instanceof MultiPlayerEntity)
			((MultiPlayerEntity) entity).updateNetwork();
	}
}
