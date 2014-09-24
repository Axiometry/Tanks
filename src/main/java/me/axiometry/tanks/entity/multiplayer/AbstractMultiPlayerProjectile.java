package me.axiometry.tanks.entity.multiplayer;

import me.axiometry.tanks.entity.*;

public abstract class AbstractMultiPlayerProjectile extends
		AbstractMultiPlayerEntity implements Projectile {
	protected Entity shooter;

	protected AbstractMultiPlayerProjectile(Entity shooter) {
		this.shooter = shooter;
	}

	@Override
	public Entity getShooter() {
		return shooter;
	}
}
