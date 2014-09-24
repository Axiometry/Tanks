package me.axiometry.tanks.world.multiplayer.protocol.readable;

import me.axiometry.tanks.entity.multiplayer.*;
import me.axiometry.tanks.server.io.protocol.ByteStream;
import me.axiometry.tanks.world.multiplayer.NetworkManager;

import java.io.IOException;

public class Packet11TankSpawn extends Packet10EntitySpawn {
	private String name;
	private double barrelRotation;

	@Override
	public byte getID() {
		return 11;
	}

	@Override
	public void readData(ByteStream stream) throws IOException {
		super.readData(stream);
		name = readString(stream);
		barrelRotation = readShort(stream) / 32D;
	}

	@Override
	public void processData(NetworkManager manager) {
		MultiPlayerTank tank = (MultiPlayerTank) EntityCache.getEntityCache()
				.newEntity(netID);
		tank.setID(id);
		tank.setX(x);
		tank.setY(y);
		tank.setRotation(rotation);
		tank.setName(name);
		tank.setBarrelRotation(barrelRotation);
		game.getWorld().spawnEntity(tank);
		tank.updatePreNetwork();
		tank.updateNetwork();
	}
}
