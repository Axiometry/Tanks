package me.axiometry.tanks.entity;

public abstract class AbstractHostileEntity extends AbstractLivingEntity
		implements HostileEntity {
	protected LivingEntity target = null;

	public LivingEntity getTarget() {
		return target;
	}
}
