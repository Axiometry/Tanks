package me.axiometry.tanks.entity.multiplayer;

import me.axiometry.tanks.entity.AbstractEntity;

public abstract class AbstractMultiPlayerEntity extends AbstractEntity
		implements MultiPlayerEntity {
	protected boolean firstNetworkUpdate;
	protected long networkUpdateTime, lastNetworkUpdateTime;
	protected double lastNetworkX, lastNetworkY, lastNetworkRotation;
	protected double targetNetworkX, targetNetworkY, targetNetworkRotation;

	@Override
	public void updatePreNetwork() {
		lastNetworkX = x;
		lastNetworkY = y;
		lastNetworkRotation = rotation;
	}

	@Override
	public void updateNetwork() {
		targetNetworkX = x;
		targetNetworkY = y;
		targetNetworkRotation = rotation;
		x = lastNetworkX;
		y = lastNetworkY;
		rotation = lastNetworkRotation;
		lastNetworkUpdateTime = networkUpdateTime;
		networkUpdateTime = System.currentTimeMillis();
		if(firstNetworkUpdate) {
			updatePreNetwork();
			firstNetworkUpdate = false;
		}
	}

	@Override
	protected final void updatePreEntity() {
		long timeElapsed = System.currentTimeMillis() - networkUpdateTime;
		long lastTimeElapsed = networkUpdateTime - lastNetworkUpdateTime;
		if(timeElapsed > lastTimeElapsed)
			return;
		double timeRatio = (double) timeElapsed / (double) lastTimeElapsed;

		interpolateNetwork(timeRatio);
	}

	protected void interpolateNetwork(double factor) {
		x = lastNetworkX + (targetNetworkX - lastNetworkX) * factor;
		y = lastNetworkY + (targetNetworkY - lastNetworkY) * factor;
		rotation = lastNetworkRotation
				+ (targetNetworkRotation - lastNetworkRotation) * factor;
	}

	protected void updatePreEntityPreNetwork() {
	}

	protected void updatePreEntityPostNetwork() {
	}

	@Override
	public double getLastNetworkX() {
		return lastNetworkX;
	}

	@Override
	public double getLastNetworkY() {
		return lastNetworkY;
	}

	@Override
	public double getLastNetworkRotation() {
		return lastNetworkRotation;
	}

	@Override
	public void setLastNetworkX(double lastNetworkX) {
		this.lastNetworkX = lastNetworkX;
	}

	@Override
	public void setLastNetworkY(double lastNetworkY) {
		this.lastNetworkY = lastNetworkY;
	}

	@Override
	public void setLastNetworkRotation(double lastNetworkRotation) {
		this.lastNetworkRotation = lastNetworkRotation;
	}

	@Override
	public double getNetworkUpdateTime() {
		return networkUpdateTime;
	}

	@Override
	public double getLastNetworkUpdateTime() {
		return lastNetworkUpdateTime;
	}
}
