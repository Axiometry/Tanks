package me.axiometry.tanks.entity.singleplayer;

import me.axiometry.tanks.entity.*;
import me.axiometry.tanks.rendering.Sprite;

public class SPBullet extends AbstractProjectile implements Bullet,
		SinglePlayerEntity {
	private static final double friction = 1.01;
	private int damage;

	protected SPBullet(Entity shooter, int damage) {
		super(shooter);
		this.damage = damage;
	}

	@Override
	protected void init() {
		width = 0.1;
		height = 0.1;
	}

	@Override
	public void updateEntity() {
		move(friction);
		if(game.getWorld().checkEntityToTileCollision(this)) {
			x -= speedX;
			y -= speedY;
			game.getWorld().spawnEntity(new Explosion(x, y, 0.5, 0));
			kill();
			return;
		}
		for(Entity entity : game.getWorld().getEntities()) {
			if(entity.equals(this)
					|| (shooter != null && entity.equals(shooter)))
				continue;
			if(game.getWorld().checkEntityToEntityCollision(this, entity)) {
				if(entity instanceof LivingEntity)
					((LivingEntity) entity).doDamage(this, damage);
				x -= speedX;
				y -= speedY;
				game.getWorld().spawnEntity(new Explosion(x, y, 0.5, 0));
				kill();
				return;
			}
		}
		if(speedX == 0 && speedY == 0)
			kill();
	}

	@Override
	public int getDamage() {
		return damage;
	}

	@Override
	public void setDamage(int damage) {
		this.damage = damage;
	}

	@Override
	public Sprite getSprite() {
		return game.getSprites().getSpriteAt(0, 5);
	}
}
