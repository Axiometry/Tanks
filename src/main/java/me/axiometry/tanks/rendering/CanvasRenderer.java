package me.axiometry.tanks.rendering;

import me.axiometry.tanks.Tanks;
import me.axiometry.tanks.util.*;
import me.axiometry.tanks.world.World;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.locks.*;

public class CanvasRenderer implements GameRenderer {
	private final Tanks game = Tanks.INSTANCE;
	private final GameCanvas canvas;
	private final InputManager inputManager;
	private final BufferedImage renderImage;
	private final Graphics2D graphics;
	private final Lock imageLock;

	public CanvasRenderer() {
		canvas = new GameCanvas(this);
		inputManager = new CanvasInputManager(this);
		renderImage = new BufferedImage(640, 480, BufferedImage.TYPE_INT_ARGB);
		graphics = renderImage.createGraphics();
		imageLock = new ReentrantLock();
		Util.awtInvokeAndWait(new Runnable() {
			@Override
			public void run() {
				game.add(canvas);
				game.validate();
				game.begin();
			}
		});
	}

	@Override
	public void update() {
	}

	@Override
	public synchronized void updateWorld(World world) {
	}

	BufferedImage getRenderImage() {
		imageLock.lock();
		BufferedImage image = renderImage;
		imageLock.unlock();
		return image;
	}

	@Override
	public void render() {
		imageLock.lock();
		int width = renderImage.getWidth();
		int height = renderImage.getHeight();
		graphics.clearRect(0, 0, width, height);
		if(game.getScreen() != null)
			game.getScreen().render(graphics, width, height);
		graphics.setColor(Color.RED);
		World world = game.getWorld();
		graphics.setFont(Constants.NEUROPOL_X.deriveFont(Font.BOLD, 15F));
		graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		Runtime runtime = Runtime.getRuntime();
		String memory = toMegabytes(runtime.totalMemory()) + "M / "
				+ toMegabytes(runtime.maxMemory()) + "M";
		graphics.drawString(
				"FPS: "
						+ game.getTimer().getFPS()
						+ " Entities: "
						+ (world != null && world.isInitialized() ? world
								.getEntities().length : "0") + " Mem: "
						+ memory, 5, 15);
		imageLock.unlock();
		canvas.repaint();
	}

	private double toMegabytes(long memory) {
		double megabytes = memory / 1000000D;
		megabytes = Math.round(megabytes * 100) / 100D;
		return megabytes;
	}

	public GameCanvas getCanvas() {
		return canvas;
	}

	@Override
	public InputManager getInputManager() {
		return inputManager;
	}
}
