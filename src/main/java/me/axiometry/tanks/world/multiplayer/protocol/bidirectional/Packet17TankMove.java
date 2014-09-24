package me.axiometry.tanks.world.multiplayer.protocol.bidirectional;

import me.axiometry.tanks.entity.multiplayer.*;
import me.axiometry.tanks.server.io.protocol.ByteStream;
import me.axiometry.tanks.world.multiplayer.NetworkManager;
import me.axiometry.tanks.world.multiplayer.protocol.WritablePacket;
import me.axiometry.tanks.world.multiplayer.protocol.readable.Packet16EntityMove;

import java.io.IOException;

public class Packet17TankMove extends Packet16EntityMove implements
		WritablePacket {
	protected double barrelRotation;

	public Packet17TankMove() {
	}

	public Packet17TankMove(MPCentralTank tank) {
		x = tank.getX();
		y = tank.getY();
		speedX = tank.getSpeedX();
		speedY = tank.getSpeedY();
		rotation = tank.getRotation();
		barrelRotation = tank.getBarrelRotation();
	}

	@Override
	public byte getID() {
		return 17;
	}

	@Override
	public void readData(ByteStream stream) throws IOException {
		super.readData(stream);
		barrelRotation = readShort(stream) / 32D;
	}

	@Override
	public void writeData(ByteStream stream) throws IOException {
		writeInt((int) (x * 32D), stream);
		writeInt((int) (y * 32D), stream);
		writeShort((short) (speedX * 32D), stream);
		writeShort((short) (speedY * 32D), stream);
		writeShort((short) ((rotation % 360) * 32D), stream);
		writeShort((short) ((barrelRotation % 360) * 32D), stream);
	}

	@Override
	public void processData(NetworkManager manager) {
		MultiPlayerTank entity = (MultiPlayerTank) game.getWorld()
				.getEntityById(id);
		if(entity == null)
			return;
		entity.updatePreNetwork();
		entity.setX(x);
		entity.setY(y);
		entity.setSpeedX(speedX);
		entity.setSpeedY(speedY);
		entity.setRotation(rotation);
		entity.setBarrelRotation(barrelRotation);
		entity.updateNetwork();
	}
}
