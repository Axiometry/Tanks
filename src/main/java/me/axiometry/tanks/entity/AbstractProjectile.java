package me.axiometry.tanks.entity;


public abstract class AbstractProjectile extends AbstractEntity implements
		Projectile {
	protected Entity shooter;

	protected AbstractProjectile(Entity shooter) {
		this.shooter = shooter;
	}

	@Override
	public Entity getShooter() {
		return shooter;
	}
}
