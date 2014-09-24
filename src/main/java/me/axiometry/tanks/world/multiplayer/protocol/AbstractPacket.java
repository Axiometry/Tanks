package me.axiometry.tanks.world.multiplayer.protocol;

import me.axiometry.tanks.Tanks;

public abstract class AbstractPacket extends ClientStreamUtils implements
		Packet {
	protected final Tanks game = Tanks.INSTANCE;
}
