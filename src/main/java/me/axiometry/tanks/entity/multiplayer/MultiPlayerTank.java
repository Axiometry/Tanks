package me.axiometry.tanks.entity.multiplayer;

import me.axiometry.tanks.entity.*;

public interface MultiPlayerTank extends Tank, MultiPlayerEntity, NamedEntity {
	public String getName();

	public void setName(String name);

	public double getLastNetworkBarrelRotation();

	public void setLastNetworkBarrelRotation(double lastNetworkBarrelRotation);
}
