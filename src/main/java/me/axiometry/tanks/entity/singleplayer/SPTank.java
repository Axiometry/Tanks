package me.axiometry.tanks.entity.singleplayer;

import me.axiometry.tanks.entity.*;
import me.axiometry.tanks.rendering.Sprite;
import me.axiometry.tanks.rendering.ui.GameOverScreen;
import me.axiometry.tanks.ui.KeyboardLayout;

import java.awt.event.*;

public class SPTank extends AbstractLivingEntity implements CentralEntity, Tank {
	private final TankSprite sprite = new TankSprite(this);

	private Direction movementDirection = Direction.NONE;
	private Direction turningDirection = Direction.NONE;
	private Direction barrelTurningDirection = Direction.NONE;
	private int treadRotationX = 0, treadRotationY = 0;
	private double barrelRotation = 0;
	private boolean fire = false;
	private boolean dropLandmine = false;
	private boolean regenerateHealth = false;
	private int deathTimer = 30;

	private int mouseX, mouseY;

	@Override
	protected void init() {
		health = 500;
		maxHealth = 500;
		x = 30;
		y = 30;
		width = 1;
		height = 1;
	}

	@Override
	public void updateEntity() {
		if(health <= 0) {
			deathTimer--;
			if(deathTimer == 0) {
				game.getWorld().destroy();
				game.setWorld(null);
				try {
					game.setScreen(new GameOverScreen());
				} catch(Exception exception) {
					exception.printStackTrace();
				}
			}
			return;
		}
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
			SPBullet bullet = new SPBullet(this, 7);
			bullet.setX(x);
			bullet.setY(y);
			bullet.setRotation(rotation + barrelRotation);
			bullet.setSpeed(1.5);
			game.getWorld().spawnEntity(bullet);
			for(int i = 0; i < 3; i++) {
				SmokeFX smoke = new SmokeFX();
				smoke.setX(x);
				smoke.setY(y);
				smoke.setRotation(rotation + barrelRotation
						+ (random.nextInt(21) - 10));
				if(movementDirection == Direction.FORWARD)
					smoke.setSpeed(TOP_SPEED + 0.25);
				else
					smoke.setSpeed(0.5);
				game.getWorld().spawnEntity(smoke);
			}
			fire = false;
		}
		if(dropLandmine) {
			SPLandmine landmine = new SPLandmine(this);
			landmine.setX(x);
			landmine.setY(y);
			landmine.setRotation(rotation);
			game.getWorld().spawnEntity(landmine);
			dropLandmine = false;
		}
		if(regenerateHealth && health < maxHealth)
			health++;
	}

	@Override
	public boolean isDead() {
		return health <= 0 && deathTimer <= 0;
	}

	@Override
	protected void onDeath() {
		game.getWorld().spawnEntity(new Explosion(x, y, 15, 20));
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
	public double getBarrelRotation() {
		return barrelRotation;
	}

	@Override
	public void setBarrelRotation(double rotation) {
		barrelRotation = rotation;
	}

	@Override
	public Sprite getSprite() {
		return sprite;
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
