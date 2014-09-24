package me.axiometry.tanks.entity.multiplayer;

import me.axiometry.tanks.entity.TankSprite;
import me.axiometry.tanks.rendering.Sprite;

public class MPOtherTank extends AbstractMultiPlayerLivingEntity implements
		MultiPlayerTank {
	private final TankSprite sprite = new TankSprite(this);

	private int treadRotationX = 0, treadRotationY = 0;
	private double barrelRotation = 0;
	private double lastNetworkBarrelRotation;

	private String name;

	public MPOtherTank() {
	}

	@Override
	public Sprite getSprite() {
		return sprite;
	}

	@Override
	public double getBarrelRotation() {
		return barrelRotation;
	}

	@Override
	public void setBarrelRotation(double barrelRotation) {
		this.barrelRotation = barrelRotation;
	}

	@Override
	public short getNetID() {
		return 2;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	protected void init() {
		health = 500;
		maxHealth = 500;
		width = 1;
		height = 1;
	}

	@Override
	protected void updateEntity() {
	}

	@Override
	protected void interpolateNetwork(double factor) {
		super.interpolateNetwork(factor);
		barrelRotation = lastNetworkBarrelRotation
				+ ((barrelRotation - lastNetworkBarrelRotation) * factor);
	}

	@Override
	public void updatePreNetwork() {
		super.updatePreNetwork();
		lastNetworkBarrelRotation = barrelRotation;
	}

	@Override
	public double getLastNetworkBarrelRotation() {
		return lastNetworkBarrelRotation;
	}

	@Override
	public void setLastNetworkBarrelRotation(double lastNetworkBarrelRotation) {
		this.lastNetworkBarrelRotation = lastNetworkBarrelRotation;
	}

	@Override
	public int getTreadRotationX() {
		return treadRotationX;
	}

	@Override
	public int getTreadRotationY() {
		return treadRotationY;
	}

	@Override
	public void setTreadRotationX(int treadRotationX) {
		this.treadRotationX = treadRotationX;
	}

	@Override
	public void setTreadRotationY(int treadRotationY) {
		this.treadRotationY = treadRotationY;
	}
}
