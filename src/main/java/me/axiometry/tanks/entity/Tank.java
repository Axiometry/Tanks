package me.axiometry.tanks.entity;

public interface Tank extends LivingEntity {
	public static final double ROTATION_SPEED = 1.5;
	public static final double FRICTION = 1.7;
	public static final double INERTIA = 3;
	public static final double TOP_SPEED = 0.25;

	public double getBarrelRotation();

	public void setBarrelRotation(double rotation);

	public int getTreadRotationX();

	public void setTreadRotationX(int treadRotationX);

	public int getTreadRotationY();

	public void setTreadRotationY(int treadRotationY);
}
