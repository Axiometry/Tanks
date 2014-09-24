package me.axiometry.tanks.rendering.ui;

import me.axiometry.tanks.Tanks;
import me.axiometry.tanks.rendering.*;
import me.axiometry.tanks.world.World;
import me.axiometry.tanks.world.multiplayer.*;

import java.awt.Graphics2D;
import java.awt.event.InputEvent;
import java.util.concurrent.*;

public class MultiPlayerScreen implements Screen {
	private final Tanks game = Tanks.INSTANCE;
	private final ExecutorService service;
	private final Sprite background;
	private int scrollAmount = 0;
	private Screen subScreen = null;
	private Screen parent;
	private MultiPlayerOptionsScreen optionsScreen;

	public MultiPlayerScreen() throws Exception {
		parent = Tanks.INSTANCE.getScreen();
		service = Executors.newSingleThreadExecutor();
		background = game.getSprites().getSpriteAt(1, 2);
		subScreen = optionsScreen = new MultiPlayerOptionsScreen(this);
	}

	public MultiPlayerScreen(Screen screen) {
		service = Executors.newSingleThreadExecutor();
		background = game.getSprites().getSpriteAt(2, 1);
		optionsScreen = new MultiPlayerOptionsScreen(this);
		subScreen = screen;
	}

	public MultiPlayerScreen(NetworkManager manager) throws Exception {
		parent = Tanks.INSTANCE.getScreen();
		service = Executors.newSingleThreadExecutor();
		background = game.getSprites().getSpriteAt(1, 2);
		optionsScreen = new MultiPlayerOptionsScreen(this);
		onConnect(manager);
	}

	public void connect(final String username, final String address,
			final int port) {
		subScreen = new MultiPlayerConnectingScreen();
		service.execute(new Runnable() {
			@Override
			public void run() {
				try {
					onConnect(new IONetworkManager(username, address, port));
				} catch(Exception exception) {
					onConnectError(exception);
				}
			}
		});
	}

	public void display() {
		subScreen = optionsScreen;
	}

	private synchronized void onConnect(NetworkManager networkManager)
			throws Exception {
		game.setWorld(new MultiPlayerWorld(networkManager));
		System.out.println("Connected!");
		if(subScreen != null
				&& subScreen instanceof MultiPlayerConnectingScreen)
			((MultiPlayerConnectingScreen) subScreen).onConnect();
		else {
			subScreen = new MultiPlayerConnectingScreen();
			((MultiPlayerConnectingScreen) subScreen).onConnect();
		}
		service.execute(new Runnable() {
			@Override
			public void run() {
				World world = game.getWorld();
				while(world == null || !world.isInitialized()) {
					try {
						Thread.sleep(200);
					} catch(InterruptedException exception) {}
					world = game.getWorld();
				}
				if(game.getWorld() == null)
					subScreen = new MultiPlayerConnectErrorScreen(
							"Error loading world");
				else
					game.setScreen(new GameScreen());
			}
		});
	}

	private synchronized void onConnectError(Exception exception) {
		subScreen = new MultiPlayerConnectErrorScreen(exception.getMessage());
		System.out.println("Error connecting: " + exception);
	}

	@Override
	public synchronized void update() {
		World world = game.getWorld();
		if(world != null && !world.isInitialized())
			world.update();
		scrollAmount++;
		if(scrollAmount >= 64)
			scrollAmount = 0;
		if(subScreen != null)
			subScreen.update();

	}

	@Override
	public void onInputEvent(InputEvent inputEvent) {
		if(subScreen != null)
			subScreen.onInputEvent(inputEvent);
	}

	@Override
	public synchronized void render(Graphics2D graphics, int width, int height) {
		for(int x = 0; x <= width / 32; x++)
			for(int y = 0; y <= height / 32; y++)
				graphics.drawImage(background.getImage(), (x * 32)
						- ((32 - (width % 32)) / 2), (y * 32)
						- ((64 - scrollAmount) / 2), null);
		if(subScreen != null)
			subScreen.render(graphics, width, height);
	}

	public Screen getSubScreen() {
		return subScreen;
	}

	public Screen getParent() {
		return parent;
	}
}
