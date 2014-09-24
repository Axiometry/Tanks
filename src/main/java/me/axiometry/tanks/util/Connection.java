package me.axiometry.tanks.util;

import java.io.*;
import java.net.*;

public class Connection {
	private String host;
	private int port;
	private Socket socket;
	private InputStream reader;
	private OutputStream writer;

	public Connection(Socket socket) {
		if(!socket.isConnected())
			throw new IllegalArgumentException("Socket must be open");
		try {
			InetAddress address = socket.getInetAddress();
			host = address.getHostAddress();
			port = socket.getPort();
			this.socket = socket;
			createReader();
			createWriter();
		} catch(IOException exception) {
			exception.printStackTrace();
		}
	}

	public Connection(String host, int port) {
		this.host = host;
		this.port = port;
		socket = new Socket();
		try {
			socket.setSoTimeout(30000);
		} catch(SocketException exception) {}
	}

	public void connect() throws IOException {
		if(socket.isConnected())
			return;
		socket.connect(new InetSocketAddress(host, port), 15000);
		createReader();
		createWriter();
	}

	private void createReader() throws IOException {
		reader = socket.getInputStream();
	}

	private void createWriter() throws IOException {
		writer = socket.getOutputStream();
	}

	public boolean disconnect() {
		if(socket.isConnected()) {
			try {
				socket.close();
			} catch(IOException e) {
				e.printStackTrace();
				return false;
			}
			reader = null;
			writer = null;
		}
		return true;
	}

	public boolean isConnected() {
		return socket.isConnected();
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public Socket getSocket() {
		return socket;
	}

	public InputStream getReader() {
		return reader;
	}

	public OutputStream getWriter() {
		return writer;
	}

}
