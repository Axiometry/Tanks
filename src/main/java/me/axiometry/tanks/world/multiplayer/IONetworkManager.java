package me.axiometry.tanks.world.multiplayer;

import me.axiometry.tanks.server.io.protocol.BufferByteStream;
import me.axiometry.tanks.util.Connection;
import me.axiometry.tanks.world.multiplayer.protocol.*;
import me.axiometry.tanks.world.multiplayer.protocol.writable.Packet2Handshake;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.concurrent.*;

public final class IONetworkManager extends AbstractNetworkManager {
	private final ExecutorService service;
	private final Connection connection;

	private final ByteBuffer readBuffer = ByteBuffer.allocate(Short.MAX_VALUE);
	private final ByteBuffer writeBuffer = ByteBuffer.allocate(Short.MAX_VALUE);

	public IONetworkManager(String username, String ip, int port)
			throws IOException {
		connection = new Connection(ip, port);
		connection.connect();

		service = Executors.newFixedThreadPool(2);
		service.execute(new ReadTask());
		service.execute(new WriteTask());

		sendPacket(new Packet2Handshake(username));
	}

	@Override
	public void shutdown(String reason) {
		service.shutdownNow();
		connection.disconnect();
		super.shutdown(reason);
	}

	private final class ReadTask implements Runnable {
		@Override
		public void run() {
			try {
				while(isRunning()) {
					InputStream in = connection.getReader();
					short size = (short) (((in.read() & 0xff) << 8) | (in
							.read() & 0xff));
					byte id = (byte) in.read();
					System.out.println("Reading packet: " + id + " size: "
							+ size);
					ReadablePacket packet = newReadablePacket(id);
					if(packet == null)
						throw new IOException("Bad packet, id " + id
								+ " (size: " + size + ")");
					readBuffer.clear();
					for(int i = 0; i < size; i++)
						readBuffer.put((byte) in.read());
					readBuffer.rewind();
					packet.readData(new BufferByteStream(readBuffer));
					System.out.println("Amount actually read: "
							+ readBuffer.position());
					readBuffer.clear();
					queueReadPacket(packet);
				}
			} catch(Exception exception) {
				exception.printStackTrace();
				shutdown(exception.getMessage());
			}
		}
	}

	private final class WriteTask implements Runnable {
		@Override
		public void run() {
			try {
				while(isRunning()) {
					OutputStream out = connection.getWriter();
					WritablePacket packet = getNextWritePacketOrWait();
					writeBuffer.clear();
					packet.writeData(new BufferByteStream(writeBuffer));
					short size = (short) writeBuffer.position();
					System.out.println("Writing packet: " + packet.getID()
							+ " size: " + size);
					out.write((size >> 8) & 0xff);
					out.write(size & 0xff);
					out.write(packet.getID());
					byte[] bytes = new byte[writeBuffer.position()];
					System.arraycopy(writeBuffer.array(), 0, bytes, 0,
							writeBuffer.position());
					out.write(bytes);
					writeBuffer.clear();
				}
			} catch(Exception exception) {
				exception.printStackTrace();
				shutdown(exception.getMessage());
			}
		}
	}
}
