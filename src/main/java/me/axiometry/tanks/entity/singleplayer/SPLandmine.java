package me.axiometry.tanks.entity.singleplayer;

import me.axiometry.tanks.entity.*;
import me.axiometry.tanks.rendering.Sprite;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class SPLandmine extends AbstractProjectile implements Landmine,
		SinglePlayerEntity {
	private final LandmineSprite sprite = new LandmineSprite();

	private int flashTimer = 0;
	private int timer = 0;
	private boolean red = true;

	public SPLandmine(Entity shooter) {
		super(shooter);
	}

	@Override
	protected void init() {
	}

	@Override
	public void updateEntity() {
		timer++;
		flashTimer++;
		if(flashTimer > (timer > 90 ? (timer > 130 ? 2 : 10) : 30)) {
			red = !red;
			flashTimer = 0;
		}
		if(timer > 160) {
			game.getWorld().spawnEntity(new Explosion(x, y, 12, 50));
			kill();
			return;
		}
	}

	@Override
	public Sprite getSprite() {
		return sprite;
	}

	private final class LandmineSprite implements Sprite {
		private final BufferedImage landmine;
		private final Point[] repaintLocations;
		private final Color darkRed;
		private final Color lightRed;

		private LandmineSprite() {
			BufferedImage image = (BufferedImage) game.getSprites()
					.getSpriteAt(0, 8).getImage();
			List<Point> locations = new ArrayList<Point>();
			int replaceColor = image.getRGB(0, 0);
			for(int x = 1; x < 31; x++)
				for(int y = 1; y < 31; y++)
					if(image.getRGB(x, y) == replaceColor)
						locations.add(new Point(x, y));
			repaintLocations = locations.toArray(new Point[locations.size()]);
			darkRed = new Color(image.getRGB(1, 0));
			lightRed = new Color(image.getRGB(2, 0));
			landmine = image.getSubimage(1, 1, 30, 30);
		}

		@Override
		public Image getImage() {
			BufferedImage newImage = new BufferedImage(30, 30,
					BufferedImage.TYPE_INT_ARGB);
			Graphics graphics = newImage.getGraphics();
			graphics.drawImage(landmine, 0, 0, null);
			graphics.setColor(red ? darkRed : lightRed);
			for(Point point : repaintLocations)
				graphics.drawLine(point.x - 1, point.y - 1, point.x - 1,
						point.y - 1);
			graphics.dispose();
			return newImage;
		}
	}
}
