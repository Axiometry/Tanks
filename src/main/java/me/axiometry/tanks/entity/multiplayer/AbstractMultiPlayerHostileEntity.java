package me.axiometry.tanks.entity.multiplayer;

import me.axiometry.tanks.entity.*;

public abstract class AbstractMultiPlayerHostileEntity extends
		AbstractMultiPlayerLivingEntity implements HostileEntity {
	protected LivingEntity target = null;

	public LivingEntity getTarget() {
		return target;
	}
}
