package me.axiometry.tanks.entity;

import me.axiometry.tanks.Tanks;

import java.awt.event.InputEvent;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractEntity implements Entity {
	protected final Tanks game = Tanks.INSTANCE;

	protected Random random = new Random();
	protected double x = 0, y = 0, rotation = 0, speedX = 0, speedY = 0,
			width = 0, height = 0;
	protected double lastX, lastY, lastRotation;

	private boolean dead = false;
	private int id;

	private static final AtomicInteger nextID = new AtomicInteger(0);

	public AbstractEntity() {
		id = nextID.getAndIncrement();
		init();
	}

	protected abstract void init();

	@Override
	public final void update() {
		lastX = x;
		lastY = y;
		lastRotation = rotation;
		updatePreEntity();
		updateEntity();
	}

	protected void updatePreEntity() {
	}

	protected abstract void updateEntity();

	@Override
	public void prerender() {
	}

	@Override
	public double getDistanceTo(Entity entity) {
		return getDistanceTo(entity.getX(), entity.getY());
	}

	@Override
	public double getDistanceTo(double x, double y) {
		return Math.sqrt(Math.pow(getX() - x, 2) + Math.pow(getY() - y, 2));
	}

	@Override
	public double getRotation() {
		return rotation;
	}

	@Override
	public double getSpeedX() {
		return speedX;
	}

	@Override
	public double getSpeedY() {
		return speedY;
	}

	@Override
	public double getX() {
		return x;
	}

	@Override
	public double getY() {
		return y;
	}

	@Override
	public double getWidth() {
		return width;
	}

	@Override
	public double getHeight() {
		return height;
	}

	@Override
	public double getLastX() {
		return lastX;
	}

	@Override
	public double getLastY() {
		return lastY;
	}

	@Override
	public double getLastRotation() {
		return lastRotation;
	}

	@Override
	public void setRotation(double rotation) {
		this.rotation = rotation;
	}

	@Override
	public void setSpeedX(double speedX) {
		this.speedX = speedX;
	}

	@Override
	public void setSpeedY(double speedY) {
		this.speedY = speedY;
	}

	@Override
	public void setX(double x) {
		this.x = x;
	}

	@Override
	public void setY(double y) {
		this.y = y;
	}

	@Override
	public void setLastX(double lastX) {
		this.lastX = lastX;
	}

	@Override
	public void setLastY(double lastY) {
		this.lastY = lastY;
	}

	@Override
	public void setLastRotation(double lastRotation) {
		this.lastRotation = lastRotation;
	}

	@Override
	public boolean isDead() {
		return dead;
	}

	@Override
	public void kill() {
		dead = true;
	}

	@Override
	public void onInputEvent(InputEvent event) {
	}

	/*
	 * Begin utility methods
	 */

	@Override
	public void setSpeed(double speed) {
		speedX = speed * Math.sin((rotation % 360) * (Math.PI / 180.0F));
		speedY = speed * -Math.cos((rotation % 360) * (Math.PI / 180.0F));
	}

	public void move(double friction) {
		x += speedX;
		y += speedY;
		if(speedX > 0) {
			speedX /= friction;
			if(speedX < 0.005)
				speedX = 0;
		} else if(speedX < 0) {
			speedX /= friction;
			if(speedX > -0.005)
				speedX = 0;
		}
		if(speedY > 0) {
			speedY /= friction;
			if(speedY < 0.005)
				speedY = 0;
		} else if(speedY < 0) {
			speedY /= friction;
			if(speedY > -0.005)
				speedY = 0;
		}
	}

	protected double getAngleTo(Entity entity) {
		double angle = Math.toDegrees(Math.atan(Math.abs(entity.getY() - y)
				/ Math.abs(entity.getX() - x)));
		if(entity.getX() - x > 0)
			if(entity.getY() - y > 0)
				return angle + 90;
			else
				return angle;
		else if(entity.getY() - y > 0)
			return angle + 180;
		return angle + 270;
	}

	@Override
	public int getID() {
		return id;
	}

	@Override
	public void setID(int id) {
		this.id = id;
	}

	@Override
	public Metadata getMetadata() {
		return new MetadataImpl();
	}

	@Override
	public void updateMetadata(Metadata metadata) {
	}
}
