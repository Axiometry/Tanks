package me.axiometry.tanks.rendering.components;

import me.axiometry.tanks.rendering.AbstractScreenComponent;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class ProgressGear extends AbstractScreenComponent {
	private static Image gearUnfilled;
	private static Image gearFilled;

	private int rotation = 0;
	private int progress;
	private int maxProgress;

	public ProgressGear(int maxProgress, int x, int y, int width, int height) {
		super(x, y, width, height);
		if(maxProgress <= 0)
			throw new IllegalArgumentException();
		this.maxProgress = maxProgress;
		if(gearUnfilled == null || gearFilled == null) {
			BufferedImage gears = guiImages.getSubimage(20, 0, 200, 100);
			gearUnfilled = gears.getSubimage(0, 0, 100, 100);
			gearFilled = gears.getSubimage(100, 0, 100, 100);
		}
	}

	@Override
	public synchronized void update() {
		rotation++;
		if(rotation >= 360)
			rotation = 0;
	}

	@Override
	public synchronized void render(Graphics2D graphics) {
		BufferedImage image = new BufferedImage(120, 120,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		AffineTransform transform = graphics.getTransform();
		g.rotate(Math.toRadians(rotation),
				10 + (gearUnfilled.getWidth(null) / 2) - 2, 10 + (gearUnfilled
						.getHeight(null) / 2) + 1);
		g.drawImage(gearUnfilled, 10, 10, null);
		int degreesProgress = (int) (360.0D * (((double) progress) / ((double) maxProgress)));
		if(degreesProgress > 0) {
			int x = (int) (60 + (100 * Math.cos(Math
					.toRadians((degreesProgress) + 270))));
			int y = (int) (60 + (100 * Math.sin(Math
					.toRadians((degreesProgress) + 270))));
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			int x2 = 0;
			int y2 = 0;
			switch(degreesProgress / 90) {
			case 0:
				x2 = 60;
				y2 = 0;
				g.setClip(0, 0, 0, 0);
				break;
			case 1:
				x2 = 120;
				y2 = 60;
				g.setClip(60, 0, 61, 61);
				break;
			case 2:
				x2 = 60;
				y2 = 120;
				g.setClip(59, 0, 61, 120);
				break;
			case 3:
				x2 = 0;
				y2 = 60;
				g.setClip(0, 59, 120, 61);
				g.drawImage(gearFilled, 10, 10, null);
				g.setClip(60, 0, 60, 60);
				break;
			case 4:
				g.setClip(0, 0, 120, 120);
			}
			g.drawImage(gearFilled, 10, 10, null);
			Polygon polygon = new Polygon(new int[] { 60, x2, x }, new int[] {
					60, y2, y }, 3);
			g.setClip(polygon);
			g.drawImage(gearFilled, 10, 10, null);
		}
		g.setTransform(transform);
		graphics
				.drawImage(image, area.x, area.y, area.width, area.height, null);
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		if(progress < 0)
			throw new IllegalArgumentException();
		if(progress > maxProgress)
			progress = maxProgress;
		this.progress = progress;
	}

	public int getMaxProgress() {
		return maxProgress;
	}

	public void setMaxProgress(int maxProgress) {
		if(maxProgress <= 0)
			throw new IllegalArgumentException();
		this.maxProgress = maxProgress;
	}
}
