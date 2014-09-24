package me.axiometry.tanks.entity.multiplayer;

import me.axiometry.tanks.entity.Entity;

public interface MultiPlayerEntity extends Entity {
	public short getNetID();

	public double getLastNetworkX();

	public double getLastNetworkY();

	public double getLastNetworkRotation();

	public double getNetworkUpdateTime();

	public double getLastNetworkUpdateTime();

	public void setLastNetworkX(double lastNetworkX);

	public void setLastNetworkY(double lastNetworkY);

	public void setLastNetworkRotation(double lastNetworkRotation);

	public void updatePreNetwork();

	public void updateNetwork();
}
