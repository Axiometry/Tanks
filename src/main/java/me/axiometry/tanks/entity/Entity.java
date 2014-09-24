package me.axiometry.tanks.entity;

import me.axiometry.tanks.rendering.Sprite;

import java.awt.event.InputEvent;

public interface Entity {
	public void update();

	public double getX();

	public double getY();

	public double getRotation();

	public double getSpeedX();

	public double getSpeedY();

	public double getWidth();

	public double getHeight();

	public double getLastX();

	public double getLastY();

	public double getLastRotation();

	public double getDistanceTo(Entity entity);

	public double getDistanceTo(double x, double y);

	public void setX(double x);

	public void setY(double y);

	public void setRotation(double rotation);

	public void setSpeedX(double speedX);

	public void setSpeedY(double speedY);

	public void setSpeed(double speed);

	public void setLastX(double lastX);

	public void setLastY(double lastY);

	public void setLastRotation(double lastRotation);

	public boolean isDead();

	public void kill();

	public void onInputEvent(InputEvent event);

	public Sprite getSprite();

	public int getID();

	void setID(int id);

	public void prerender();

	public Metadata getMetadata();

	public void updateMetadata(Metadata metadata);
}
