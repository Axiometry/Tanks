package me.axiometry.tanks.rendering;

import java.awt.*;
import java.awt.image.BufferStrategy;

public class GameCanvas extends Canvas {
	private static final long serialVersionUID = -7698088540417383540L;

	private CanvasRenderer renderer;
	private BufferStrategy strategy;

	public GameCanvas(CanvasRenderer renderer) {
		setSize(640, 480);
		this.renderer = renderer;
	}

	@Override
	public void addNotify() {
		super.addNotify();
		if(strategy != null)
			return;
		setIgnoreRepaint(true);
		createBufferStrategy(2);
		strategy = getBufferStrategy();
	}

	@Override
	public void removeNotify() {
		super.removeNotify();
		strategy = null;
	}

	@Override
	public synchronized void paint(Graphics g) {
		if(strategy == null)
			return;
		Graphics2D graphics = (Graphics2D) strategy.getDrawGraphics();
		graphics.drawImage(renderer.getRenderImage(), 0, 0, this);
		graphics.dispose();
		strategy.show();
	}

	@Override
	public void update(Graphics g) {
		paint(g);
	}

	@Override
	public void repaint() {
		update(getGraphics());
	}

	public CanvasRenderer getRenderer() {
		return renderer;
	}
}
