package me.axiometry.tanks.entity.singleplayer;

import me.axiometry.tanks.entity.*;
import me.axiometry.tanks.rendering.*;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public final class SPTurret extends AbstractHostileEntity implements Turret,
		SinglePlayerEntity {
	private final TurretSprite sprite = new TurretSprite();
	private int gunRotation = 0;
	private int fireTimer = 0;

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
		if(game.getWorld() == null)
			return;
		Tank closest = null;
		double closestDistance = Double.MAX_VALUE;
		for(Entity entity : game.getWorld().getEntities()) {
			if(!(entity instanceof Tank))
				continue;
			double distance = getDistanceTo(entity);
			if(distance < closestDistance) {
				closestDistance = distance;
				closest = (Tank) entity;
			}
		}
		target = closest;
		if(closest == null)
			return;
		double newRotation = Math.toDegrees(Math.atan(Math.abs(closest.getY()
				- y)
				/ Math.abs(closest.getX() - x)));
		gunRotation = (int) (360 - (newRotation + 90));
		if(closest.getX() - x > 0) {
			if(closest.getY() - y > 0) {
				gunRotation += 90;
				gunRotation = 360 - gunRotation;
				gunRotation += 90;
			} else
				gunRotation += 180;
		} else if(closest.getY() - y < 0) {
			gunRotation -= 90;
			gunRotation = 180 - gunRotation;
			gunRotation += 270;
		}
		fireTimer += 2;
		if(fireTimer > 50) {
			SPBullet sPBullet = new SPBullet(this, 2);
			sPBullet.setX(x);
			sPBullet.setY(y);
			sPBullet.setSpeedX(1.5 * Math.sin(((gunRotation) % 360)
					* (Math.PI / 180.0F)));
			sPBullet.setSpeedY(1.5 * -Math.cos(((gunRotation) % 360)
					* (Math.PI / 180.0F)));
			sPBullet.setRotation(gunRotation);
			game.getWorld().spawnEntity(sPBullet);
			fireTimer = 0;
		}
	}

	@Override
	public boolean doDamage(Entity source, int damage) {
		if(source != null && source instanceof SPBullet
				&& ((SPBullet) source).getShooter() instanceof SPTurret) {
			return false;
		}
		return super.doDamage(source, damage);
	}

	@Override
	protected void onDeath() {
		game.getWorld().spawnEntity(new Explosion(x, y, 4, 4));
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

			AffineTransform transform = new AffineTransform();
			transform.setToIdentity();
			transform.rotate(Math.toRadians(gunRotation), 24, 20);
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			g.transform(transform);

			g.drawImage(gun.getImage(), 8, 0, null);
			g.dispose();
			return image;
		}
	}
}
