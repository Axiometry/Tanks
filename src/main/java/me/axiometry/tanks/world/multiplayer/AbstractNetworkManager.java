package me.axiometry.tanks.world.multiplayer;

import me.axiometry.tanks.Tanks;
import me.axiometry.tanks.rendering.Screen;
import me.axiometry.tanks.rendering.ui.*;
import me.axiometry.tanks.util.IntHashMap;
import me.axiometry.tanks.world.World;
import me.axiometry.tanks.world.multiplayer.protocol.*;
import me.axiometry.tanks.world.multiplayer.protocol.bidirectional.*;
import me.axiometry.tanks.world.multiplayer.protocol.readable.*;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.*;

public abstract class AbstractNetworkManager implements NetworkManager {
	private final IntHashMap<Class<? extends ReadablePacket>> readablePackets;
	private final Queue<ReadablePacket> packetProcessQueue;
	private final Queue<WritablePacket> packetWriteQueue;

	private boolean running = true;

	public AbstractNetworkManager() throws IOException {
		readablePackets = new IntHashMap<Class<? extends ReadablePacket>>();
		packetProcessQueue = new ArrayDeque<ReadablePacket>();
		packetWriteQueue = new ArrayDeque<WritablePacket>();

		defineDefaultPackets();
	}

	private void defineDefaultPackets() {
		if(readablePackets.size() > 0)
			throw new IllegalStateException();
		defineReadablePacket(Packet1Ping.class);
		defineReadablePacket(Packet3WorldUpdate.class);
		defineReadablePacket(Packet10EntitySpawn.class);
		defineReadablePacket(Packet11TankSpawn.class);
		defineReadablePacket(Packet12FxSpawn.class);
		defineReadablePacket(Packet15EntityDespawn.class);
		defineReadablePacket(Packet16EntityMove.class);
		defineReadablePacket(Packet17TankMove.class);
		defineReadablePacket(Packet21HealthUpdate.class);
	}

	@Override
	public synchronized void defineReadablePacket(
			Class<? extends ReadablePacket> packetClass) {
		if(packetClass == null)
			throw new NullPointerException("Null packet");
		Constructor<? extends ReadablePacket> constructor;
		try {
			constructor = packetClass.getConstructor();
		} catch(Exception exception1) {
			throw new IllegalArgumentException("No default constructor");
		}
		Packet packet;
		try {
			packet = constructor.newInstance();
		} catch(Exception exception) {
			throw new IllegalArgumentException(exception);
		}
		byte id = packet.getID();
		if(getReadablePacketClass(id) != null)
			throw new IllegalArgumentException("Duplicate packet id");
		readablePackets.put(id, packetClass);
	}

	protected ReadablePacket newReadablePacket(byte id) {
		try {
			return getReadablePacketClass(id).newInstance();
		} catch(Exception exception) {}
		return null;
	}

	protected Class<? extends ReadablePacket> getReadablePacketClass(byte id) {
		return readablePackets.get(id);
	}

	@Override
	public synchronized void sendPacket(WritablePacket packet) {
		queueWritePacket(packet);
	}

	@Override
	public synchronized void processPackets() {
		List<ReadablePacket> packetsToProcess = new ArrayList<ReadablePacket>();
		synchronized(packetProcessQueue) {
			while(!packetProcessQueue.isEmpty())
				packetsToProcess.add(packetProcessQueue.poll());
		}
		try {
			for(ReadablePacket packet : packetsToProcess)
				packet.processData(this);
		} catch(Exception exception) {
			exception.printStackTrace();
			shutdown(exception.getMessage());
		}
	}

	protected void queueReadPacket(ReadablePacket packet) {
		synchronized(packetProcessQueue) {
			packetProcessQueue.offer(packet);
			packetProcessQueue.notifyAll();
		}
	}

	protected void queueWritePacket(WritablePacket packet) {
		synchronized(packetWriteQueue) {
			packetWriteQueue.offer(packet);
			packetWriteQueue.notifyAll();
		}
	}

	protected ReadablePacket getNextReadPacket() {
		synchronized(packetProcessQueue) {
			return packetProcessQueue.poll();
		}
	}

	protected ReadablePacket getNextReadPacketOrWait() {
		while(isRunning()) {
			synchronized(packetProcessQueue) {
				ReadablePacket packet = packetProcessQueue.poll();
				if(packet == null) {
					try {
						packetProcessQueue.wait(2000);
					} catch(InterruptedException exception) {}
				} else
					return packet;
			}
		}
		return null;
	}

	protected WritablePacket getNextWritePacket() {
		synchronized(packetWriteQueue) {
			return packetWriteQueue.poll();
		}
	}

	protected WritablePacket getNextWritePacketOrWait() {
		while(isRunning()) {
			try {
				System.out.println(">*Start checking");
				synchronized(packetWriteQueue) {
					System.out.println(">**sync");
					WritablePacket packet = packetWriteQueue.poll();
					if(packet == null) {
						System.out.println(">**waiting");
						try {
							packetWriteQueue.wait(2000);
						} catch(InterruptedException exception) {}
						System.out.println(">**done waiting");
					} else {
						System.out.println(">**returning!");
						return packet;
					}
				}
			} finally {
				System.out.println(">*Done checking");
			}
		}
		return null;
	}

	@Override
	public boolean isRunning() {
		return running;
	}

	@Override
	public void shutdown(String reason) {
		running = false;
		Tanks game = Tanks.INSTANCE;
		Screen screen = game.getScreen();
		World world = game.getWorld();
		if(screen instanceof GameScreen && world instanceof MultiPlayerWorld
				&& ((MultiPlayerWorld) world).getNetworkManager().equals(this))
			game.setScreen(new MultiPlayerScreen(
					new MultiPlayerConnectErrorScreen(reason)));
		System.out.println("Shutdown: " + reason);
	}
}
