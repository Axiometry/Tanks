package me.axiometry.tanks.entity.multiplayer;

import me.axiometry.tanks.entity.Turret;
import me.axiometry.tanks.rendering.*;

import java.awt.*;
import java.awt.image.BufferedImage;

public final class MPTurret extends AbstractMultiPlayerHostileEntity implements
		Turret {
	private final TurretSprite sprite = new TurretSprite();
	private double gunRotation = 0;

	@Override
	public short getNetID() {
		return 4;
	}

	@Override
	protected void init() {
		health = 25;
		maxHealth = 25;
		x = 10.5;
		y = 10.5;
		width = 1;
		height = 1;
	}

	@Override
	public void updateEntity() {
		gunRotation = rotation;
		rotation = 0;
	}

	@Override
	public Sprite getSprite() {
		return sprite;
	}

	private class TurretSprite implements Sprite {
		private final Sprite basePlate;
		private final Sprite gun;

		public TurretSprite() {
			SpriteMap sprites = game.getSprites();
			basePlate = sprites.getSpriteAt(0, 7);
			gun = sprites.getSpriteAt(0, 6);
		}

		@Override
		public Image getImage() {
			BufferedImage image = new BufferedImage(48, 48,
					BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = image.createGraphics();
			g.drawImage(basePlate.getImage(), 8, 4, null);
			g.rotate(Math.toRadians(gunRotation), 24, 20);
			g.drawImage(gun.getImage(), 8, 0, null);
			g.dispose();
			return image;
		}
	}
}
