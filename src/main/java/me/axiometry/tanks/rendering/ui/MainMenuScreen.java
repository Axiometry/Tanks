package me.axiometry.tanks.rendering.ui;

import me.axiometry.tanks.Tanks;
import me.axiometry.tanks.rendering.*;
import me.axiometry.tanks.rendering.components.Button;
import me.axiometry.tanks.world.multiplayer.MemoryNetworkManager;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

public class MainMenuScreen implements Screen {
	private final Tanks game = Tanks.INSTANCE;
	private final Sprite background;
	private final Image gear;
	private final Image logo;
	private final int entireLogoSize;
	private final Button singlePlayer, multiPlayer, info, quit;
	private final MultiPlayerScreen multiPlayerScreen = new MultiPlayerScreen();

	private int gearRotation = 0;
	private int scrollAmount = 0;

	public MainMenuScreen() throws Exception {
		background = game.getSprites().getSpriteAt(1, 2);
		BufferedImage logo = ImageIO.read(getClass().getResourceAsStream(
				"/logo.png"));
		gear = logo.getSubimage(0, 0, 100, 100);
		this.logo = logo.getSubimage(100, 0, 300, 100);
		entireLogoSize = logo.getWidth();
		singlePlayer = new Button("Single Player", new Runnable() {
			@Override
			public void run() {
				try {
					game.setScreen(new SinglePlayerScreen());
					if("".equals(""))
						return;
					final MemoryNetworkManager networkManager = new MemoryNetworkManager(
							"testlol");
					game.setScreen(new MultiPlayerScreen(networkManager));
					new Thread(new Runnable() {

						@Override
						public void run() {
							try {
								game.startServer(networkManager);
							} catch(Exception exception) {
								exception.printStackTrace();
							}
						}
					}).start();
				} catch(Exception exception) {
					exception.printStackTrace();
				}
			}
		}, 0, 0, 350, 40);
		multiPlayer = new Button("Multi Player", new Runnable() {
			@Override
			public void run() {
				try {
					multiPlayerScreen.display();
					game.setScreen(multiPlayerScreen);
				} catch(Exception exception) {
					exception.printStackTrace();
				}
			}
		}, 0, 0, 350, 40);
		info = new Button("Info", null, 0, 0, 170, 40);
		quit = new Button("Quit", new Runnable() {
			@Override
			public void run() {
				game.stop();
			}
		}, 0, 0, 170, 40);
	}

	@Override
	public synchronized void update() {
		scrollAmount++;
		if(scrollAmount >= 64)
			scrollAmount = 0;
		gearRotation += 2;
		if(gearRotation >= 360)
			gearRotation = 0;
	}

	@Override
	public void onInputEvent(InputEvent inputEvent) {
		singlePlayer.onInputEvent(inputEvent);
		multiPlayer.onInputEvent(inputEvent);
		info.onInputEvent(inputEvent);
		quit.onInputEvent(inputEvent);
	}

	@Override
	public synchronized void render(Graphics2D graphics, int width, int height) {
		for(int x = 0; x <= width / 32; x++)
			for(int y = 0; y <= height / 32; y++)
				graphics.drawImage(background.getImage(), (x * 32)
						- ((32 - (width % 32)) / 2), (y * 32)
						- ((64 - scrollAmount) / 2), null);
		AffineTransform transform = graphics.getTransform();
		AffineTransform rotation = new AffineTransform(transform);
		rotation.setToIdentity();
		rotation.rotate(
				Math.toRadians(gearRotation),
				(width / 2) - (entireLogoSize / 2) + (gear.getWidth(null) / 2)
						- 2,
				(height / 2) - logo.getHeight(null) - 74
						+ (gear.getHeight(null) / 2));
		graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.setTransform(rotation);
		graphics.drawImage(gear, (width / 2) - (entireLogoSize / 2),
				(height / 2) - logo.getHeight(null) - 75, null);
		graphics.setTransform(transform);
		graphics.drawImage(logo,
				(width / 2) - (entireLogoSize / 2) + gear.getWidth(null),
				(height / 2) - logo.getHeight(null) - 75, null);
		singlePlayer.setX((width / 2) - (singlePlayer.getWidth() / 2));
		singlePlayer.setY((height / 2) - singlePlayer.getHeight() - 10);
		singlePlayer.render(graphics);
		multiPlayer.setX((width / 2) - (multiPlayer.getWidth() / 2));
		multiPlayer.setY((height / 2));
		multiPlayer.render(graphics);
		info.setX((width / 2) - info.getWidth() - 5);
		info.setY((height / 2) + 50);
		info.render(graphics);
		quit.setX((width / 2) + 5);
		quit.setY((height / 2) + 50);
		quit.render(graphics);
	}
}
