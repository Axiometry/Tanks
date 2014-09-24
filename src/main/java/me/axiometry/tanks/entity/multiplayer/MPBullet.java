package me.axiometry.tanks.entity.multiplayer;

import me.axiometry.tanks.entity.Bullet;
import me.axiometry.tanks.rendering.Sprite;

public class MPBullet extends AbstractMultiPlayerProjectile implements Bullet {
	public MPBullet() {
		super(null);
	}

	@Override
	protected void updateEntity() {
	}

	public short getNetID() {
		return 5;
	}

	@Override
	public int getDamage() {
		return 0;
	}

	@Override
	public void setDamage(int damage) {
	}

	@Override
	protected void init() {
	}

	@Override
	public Sprite getSprite() {
		return game.getSprites().getSpriteAt(0, 5);
	}
}
