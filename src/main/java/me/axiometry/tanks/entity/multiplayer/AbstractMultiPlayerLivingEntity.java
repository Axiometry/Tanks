package me.axiometry.tanks.entity.multiplayer;

import me.axiometry.tanks.entity.*;

public abstract class AbstractMultiPlayerLivingEntity extends
		AbstractMultiPlayerEntity implements LivingEntity {
	protected int health, maxHealth;

	@Override
	public int getHealth() {
		return health;
	}

	@Override
	public int getMaxHealth() {
		return maxHealth;
	}

	@Override
	public boolean isDead() {
		return health <= 0;
	}

	@Override
	public void kill() {
		health = 0;
	}

	@Override
	public void setHealth(int health) {
		if(isDead())
			return;
		boolean fireDeathEvent = this.health > 0 && health <= 0;
		this.health = health;
		if(fireDeathEvent)
			onDeath();
	}

	@Override
	public boolean doDamage(Entity source, int damage) {
		setHealth(health - damage);
		return true;
	}

	@Override
	public boolean doDamage(int damage) {
		return doDamage(null, damage);
	}

	protected void onDeath() {
	}
}
