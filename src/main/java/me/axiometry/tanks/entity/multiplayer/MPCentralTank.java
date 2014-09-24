package me.axiometry.tanks.entity.multiplayer;

import me.axiometry.tanks.entity.*;
import me.axiometry.tanks.rendering.*;
import me.axiometry.tanks.rendering.ui.*;
import me.axiometry.tanks.ui.KeyboardLayout;
import me.axiometry.tanks.world.multiplayer.MultiPlayerWorld;
import me.axiometry.tanks.world.multiplayer.protocol.bidirectional.Packet17TankMove;
import me.axiometry.tanks.world.multiplayer.protocol.writable.Packet20Fire;

import java.awt.event.*;

public class MPCentralTank extends AbstractMultiPlayerLivingEntity implements
		MultiPlayerTank, CentralEntity {
	private final MultiPlayerWorld world = (MultiPlayerWorld) game.getWorld();
	private final TankSprite sprite = new TankSprite(this);

	private Direction movementDirection = Direction.NONE;
	private Direction turningDirection = Direction.NONE;
	private Direction barrelTurningDirection = Direction.NONE;

	private int treadRotationX = 0, treadRotationY = 0;
	private double barrelRotation = 0;
	private boolean fire = false;
	private boolean dropLandmine = false;

	private int mouseX, mouseY;

	private String name;

	public MPCentralTank() {
		System.out.println("SPAWNED");
		Screen screen = game.getScreen();
		if(screen != null && screen instanceof MultiPlayerGameOverScreen) {
			((MultiPlayerGameOverScreen) screen).despawnDummy();
			game.setScreen(((MultiPlayerGameOverScreen) screen).getGameScreen());
		}
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
		return 1;
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
	protected void interpolateNetwork(double factor) {
	}

	@Override
	public void updateEntity() {
		if(turningDirection == Direction.LEFT)
			rotation -= ROTATION_SPEED;
		else if(turningDirection == Direction.RIGHT)
			rotation += ROTATION_SPEED;

		if(barrelTurningDirection == Direction.LEFT)
			barrelRotation -= ROTATION_SPEED * 2;
		else if(barrelTurningDirection == Direction.RIGHT)
			barrelRotation += ROTATION_SPEED * 2;
		if(barrelRotation >= 360)
			barrelRotation = 0;
		else if(barrelRotation < 0)
			barrelRotation = 359;

		if(movementDirection == Direction.FORWARD) {
			double maxSpeedX = TOP_SPEED
					* Math.sin(rotation * (Math.PI / 180.0F));
			double maxSpeedY = TOP_SPEED
					* -Math.cos(rotation * (Math.PI / 180.0F));
			double nextSpeedX = speedX + (maxSpeedX / INERTIA);
			double nextSpeedY = speedY + (maxSpeedY / INERTIA);
			if(Math.abs(nextSpeedX) <= Math.abs(maxSpeedX))
				speedX = nextSpeedX;
			else if(Math.abs(speedX) > Math.abs(maxSpeedX))
				speedX = maxSpeedX;
			if(Math.abs(nextSpeedY) <= Math.abs(maxSpeedY))
				speedY = nextSpeedY;
			else if(Math.abs(speedY) > Math.abs(maxSpeedY))
				speedY = maxSpeedY;
		} else if(movementDirection == Direction.BACKWARD) {
			double maxSpeedX = -TOP_SPEED
					* Math.sin(rotation * (Math.PI / 180.0F));
			double maxSpeedY = -TOP_SPEED
					* -Math.cos(rotation * (Math.PI / 180.0F));
			double nextSpeedX = speedX + (maxSpeedX / INERTIA);
			double nextSpeedY = speedY + (maxSpeedY / INERTIA);
			if(Math.abs(nextSpeedX) <= Math.abs(maxSpeedX))
				speedX = nextSpeedX;
			else if(Math.abs(speedX) > Math.abs(maxSpeedX))
				speedX = maxSpeedX;
			if(Math.abs(nextSpeedY) <= Math.abs(maxSpeedY))
				speedY = nextSpeedY;
			else if(Math.abs(speedY) > Math.abs(maxSpeedY))
				speedY = maxSpeedY;
		} else {
			if(speedX > 0) {
				speedX /= FRICTION;
				if(speedX < 0.005)
					speedX = 0;
			} else if(speedX < 0) {
				speedX /= FRICTION;
				if(speedX > -0.005)
					speedX = 0;
			}
			if(speedY > 0) {
				speedY /= FRICTION;
				if(speedY < 0.005)
					speedY = 0;
			} else if(speedY < 0) {
				speedY /= FRICTION;
				if(speedY > -0.005)
					speedY = 0;
			}
		}

		x += speedX;
		y += speedY;
		if(game.getWorld().checkEntityCollision(this)) {
			x -= speedX;
			y -= speedY;
			x += speedX / 3;
			y += speedY / 3;
			if(game.getWorld().checkEntityCollision(this)) {
				x -= speedX / 3;
				y -= speedY / 3;
				speedX = 0;
				speedY = 0;
			} else {
				speedX /= 3;
				speedY /= 3;
			}
		}

		barrelRotation = (Math.atan2(mouseY - (game.getHeight() / 2), mouseX
				- (game.getWidth() / 2)) * (180D / Math.PI))
				+ 90 - rotation;

		int treadChange = (int) ((Math.abs(speedX) + Math.abs(speedY)) * 30.0);
		if(movementDirection.equals(Direction.FORWARD)) {
			treadRotationX -= treadChange;
			treadRotationY -= treadChange;
		} else if(movementDirection.equals(Direction.BACKWARD)) {
			treadRotationX += treadChange;
			treadRotationY += treadChange;
		}
		int treadChangeTurning = (int) (ROTATION_SPEED * 4.0);
		if(turningDirection.equals(Direction.LEFT)) {
			treadRotationX += treadChangeTurning;
			treadRotationY -= treadChangeTurning;
		} else if(turningDirection.equals(Direction.RIGHT)) {
			treadRotationX -= treadChangeTurning;
			treadRotationY += treadChangeTurning;
		}
		if(treadRotationX >= 30)
			treadRotationX = 0;
		else if(treadRotationX < 0)
			treadRotationX = 29;
		if(treadRotationY >= 30)
			treadRotationY = 0;
		else if(treadRotationY < 0)
			treadRotationY = 29;

		if(fire) {
			world.getNetworkManager().sendPacket(
					new Packet20Fire(Packet20Fire.WeaponType.BULLET, rotation
							+ barrelRotation));
			fire = false;
		}
		if(dropLandmine) {
			world.getNetworkManager().sendPacket(
					new Packet20Fire(Packet20Fire.WeaponType.CHARGE, rotation));
			dropLandmine = false;
		}
		world.getNetworkManager().sendPacket(new Packet17TankMove(this));
	}

	@Override
	public double getLastNetworkBarrelRotation() {
		return barrelRotation;
	}

	@Override
	public void setLastNetworkBarrelRotation(double lastNetworkBarrelRotation) {
		barrelRotation = lastNetworkBarrelRotation;
	}

	@Override
	public void onInputEvent(InputEvent event) {
		if(event instanceof KeyEvent) {
			int key = ((KeyEvent) event).getKeyCode();
			KeyboardLayout layout = game.getKeyboardLayout();
			if(key == layout.getKeyUp()) {
				if(((KeyEvent) event).getID() == KeyEvent.KEY_PRESSED)
					movementDirection = Direction.FORWARD;
				else if(((KeyEvent) event).getID() == KeyEvent.KEY_RELEASED)
					movementDirection = Direction.NONE;
			} else if(key == layout.getKeyDown()) {
				if(((KeyEvent) event).getID() == KeyEvent.KEY_PRESSED)
					movementDirection = Direction.BACKWARD;
				else if(((KeyEvent) event).getID() == KeyEvent.KEY_RELEASED)
					movementDirection = Direction.NONE;
			} else if(key == layout.getKeyLeft()) {
				if(((KeyEvent) event).getID() == KeyEvent.KEY_PRESSED)
					turningDirection = Direction.LEFT;
				else if(((KeyEvent) event).getID() == KeyEvent.KEY_RELEASED)
					turningDirection = Direction.NONE;
			} else if(key == layout.getKeyRight()) {
				if(((KeyEvent) event).getID() == KeyEvent.KEY_PRESSED)
					turningDirection = Direction.RIGHT;
				else if(((KeyEvent) event).getID() == KeyEvent.KEY_RELEASED)
					turningDirection = Direction.NONE;
			} else if(key == layout.getKeyLeftBarrel()) {
				if(((KeyEvent) event).getID() == KeyEvent.KEY_PRESSED)
					barrelTurningDirection = Direction.LEFT;
				else if(((KeyEvent) event).getID() == KeyEvent.KEY_RELEASED)
					barrelTurningDirection = Direction.NONE;
			} else if(key == layout.getKeyRightBarrel()) {
				if(((KeyEvent) event).getID() == KeyEvent.KEY_PRESSED)
					barrelTurningDirection = Direction.RIGHT;
				else if(((KeyEvent) event).getID() == KeyEvent.KEY_RELEASED)
					barrelTurningDirection = Direction.NONE;
			} else if(key == layout.getKeyFire()) {
				if(((KeyEvent) event).getID() == KeyEvent.KEY_PRESSED)
					fire = true;
			} else if(key == layout.getKeyDropLandmine())
				if(((KeyEvent) event).getID() == KeyEvent.KEY_PRESSED)
					dropLandmine = true;
		} else if(event instanceof MouseEvent) {
			MouseEvent mouseEvent = (MouseEvent) event;
			if(mouseEvent.getID() == MouseEvent.MOUSE_MOVED
					|| mouseEvent.getID() == MouseEvent.MOUSE_DRAGGED) {
				mouseX = mouseEvent.getX();
				mouseY = mouseEvent.getY();
			} else if(mouseEvent.getID() == MouseEvent.MOUSE_PRESSED) {
				if(mouseEvent.getButton() == MouseEvent.BUTTON1)
					fire = true;
				else if(mouseEvent.getButton() == MouseEvent.BUTTON3)
					dropLandmine = true;
			}
		}
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

	@Override
	public boolean doDamage(Entity source, int damage) {
		int previousHealth = health;
		boolean damaged = super.doDamage(source, damage);
		Screen screen = game.getScreen();
		if(damaged && previousHealth > 0 && health <= 0 && screen != null
				&& screen instanceof GameScreen)
			game.setScreen(new MultiPlayerGameOverScreen(this,
					(GameScreen) screen));
		return damaged;
	}

	@Override
	public void setHealth(int health) {
		Screen screen = game.getScreen();
		if(this.health > 0 && health <= 0 && screen != null
				&& screen instanceof GameScreen)
			game.setScreen(new MultiPlayerGameOverScreen(this,
					(GameScreen) screen));
		super.setHealth(health);
	}
}
