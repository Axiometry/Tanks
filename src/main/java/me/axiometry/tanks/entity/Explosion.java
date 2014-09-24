package me.axiometry.tanks.entity;

import me.axiometry.tanks.rendering.*;

public class Explosion extends AbstractEntity {
	private int timer = 0;
	private double yeild = 0;
	private int damage = 0;

	public Explosion(double x, double y, double yeild, int damage) {
		this.x = x;
		this.y = y;
		this.yeild = yeild;
		this.damage = damage;
	}

	@Override
	protected void init() {
	}

	@Override
	protected void updateEntity() {
		if(damage > 0 && timer == 2) {
			double radius = yeild * 1.2;
			for(Entity entity : game.getWorld().getEntities()) {
				double distance = getDistanceTo(entity);
				if(entity instanceof LivingEntity && distance <= radius) {
					double percent = (radius - distance) / radius;
					((LivingEntity) entity).doDamage((int) (damage * percent));
					double currentRotation = entity.getRotation();
					entity.setRotation(getAngleTo(entity));
					entity.setSpeed(yeild * 0.5 * percent);
					entity.setRotation(currentRotation);
				}
			}
		}
		for(int i = 0; i < 3 * yeild; i++) {
			SmokeFX smoke = new SmokeFX();
			smoke.setX(x);
			smoke.setY(y);
			smoke.setRotation(random.nextInt(360));
			smoke.setSpeed(0.2 * (random.nextDouble() * yeild));
			game.getWorld().spawnEntity(smoke);
		}
		timer++;
		if(timer > 5)
			kill();
	}

	@Override
	public Sprite getSprite() {
		return new EmptySprite();
	}

}
