package me.axiometry.tanks.world.multiplayer;

import me.axiometry.tanks.world.multiplayer.protocol.*;

public interface NetworkManager {
	public void defineReadablePacket(Class<? extends ReadablePacket> packet);

	public void sendPacket(WritablePacket packet);

	public void processPackets();

	public boolean isRunning();

	public void shutdown(String reason);
}
