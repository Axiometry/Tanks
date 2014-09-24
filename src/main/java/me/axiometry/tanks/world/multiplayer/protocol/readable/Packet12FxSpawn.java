package me.axiometry.tanks.world.multiplayer.protocol.readable;

import me.axiometry.tanks.entity.FXEntity;
import me.axiometry.tanks.entity.multiplayer.EntityCache;
import me.axiometry.tanks.server.io.protocol.ByteStream;
import me.axiometry.tanks.world.World;
import me.axiometry.tanks.world.multiplayer.NetworkManager;
import me.axiometry.tanks.world.multiplayer.protocol.*;

import java.io.IOException;
import java.util.Random;

public class Packet12FxSpawn extends AbstractPacket implements ReadablePacket {
	private short netID;
	private double x, y, speed, rotation;
	private int degreesFreedom, amount;

	@Override
	public byte getID() {
		return 12;
	}

	@Override
	public void readData(ByteStream stream) throws IOException {
		netID = readShort(stream);
		x = readInt(stream) / 32D;
		y = readInt(stream) / 32D;
		speed = readInt(stream) / 32D;
		rotation = readShort(stream) / 32D;
		degreesFreedom = readShort(stream);
		amount = readShort(stream);
	}

	@Override
	public void processData(NetworkManager manager) {
		World world = game.getWorld();
		if(world == null)
			return;
		Random random = new Random();
		for(int i = 0; i < amount; i++) {
			FXEntity fx = (FXEntity) EntityCache.getEntityCache().newEntity(
					netID);
			fx.setX(x);
			fx.setY(y);
			fx.setRotation(rotation
					+ (degreesFreedom == 0 ? 0 : random.nextInt(degreesFreedom)));
			fx.setSpeed(0.2 * (random.nextDouble() * speed));
			world.spawnEntity(fx);
		}
	}
}
