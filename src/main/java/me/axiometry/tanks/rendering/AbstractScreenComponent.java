package me.axiometry.tanks.rendering;

import java.awt.Rectangle;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public abstract class AbstractScreenComponent implements ScreenComponent {
	protected BufferedImage guiImages;

	protected Rectangle area;

	private boolean lastInside = false;
	private boolean lastPressed = false;

	protected AbstractScreenComponent(int x, int y, int width, int height) {
		if(guiImages == null) {
			try {
				guiImages = ImageIO.read(getClass().getResourceAsStream(
						"/gui.png"));
			} catch(IOException exception) {}
		}
		area = new Rectangle(x, y, width, height);
	}

	@Override
	public void update() {
	}

	@Override
	public boolean contains(int x, int y) {
		return area.contains(x, y);
	}

	@Override
	public int getX() {
		return area.x;
	}

	@Override
	public int getY() {
		return area.y;
	}

	@Override
	public int getWidth() {
		return area.width;
	}

	@Override
	public int getHeight() {
		return area.height;
	}

	@Override
	public void setX(int x) {
		area.x = x;
	}

	@Override
	public void setY(int y) {
		area.y = y;
	}

	@Override
	public void setWidth(int width) {
		area.width = width;
	}

	@Override
	public void setHeight(int height) {
		area.height = height;
	}

	@Override
	public void onInputEvent(InputEvent event) {
		if(event instanceof MouseEvent) {
			MouseEvent mouseEvent = (MouseEvent) event;
			if(mouseEvent.getID() == MouseEvent.MOUSE_MOVED) {
				onMouseMove(mouseEvent.getX(), mouseEvent.getY());
				if(area.contains(mouseEvent.getPoint()) && !lastInside) {
					lastInside = true;
					onMouseEnter(mouseEvent.getX(), mouseEvent.getY());
				} else if(!area.contains(((MouseEvent) event).getPoint())
						&& lastInside) {
					lastInside = false;
					onMouseExit(mouseEvent.getX(), mouseEvent.getY());
				}
			} else if(mouseEvent.getID() == MouseEvent.MOUSE_PRESSED) {
				if(area.contains(mouseEvent.getPoint())) {
					lastPressed = true;
					onMousePress(mouseEvent.getX(), mouseEvent.getY(),
							mouseEvent.getButton());
				}
			} else if(mouseEvent.getID() == MouseEvent.MOUSE_RELEASED
					&& lastPressed) {
				lastPressed = false;
				onMouseRelease(mouseEvent.getX(), mouseEvent.getY(),
						mouseEvent.getButton());
			} else if(mouseEvent.getID() == MouseEvent.MOUSE_DRAGGED) {
				onMouseDrag(mouseEvent.getX(), mouseEvent.getY());
			}
		} else if(event instanceof KeyEvent) {
			KeyEvent keyEvent = (KeyEvent) event;
			if(keyEvent.getID() == KeyEvent.KEY_TYPED)
				onKeyTyped(keyEvent.getKeyChar(), keyEvent.getKeyCode(),
						keyEvent.getModifiers());
			else if(keyEvent.getID() == KeyEvent.KEY_PRESSED)
				onKeyPressed(keyEvent.getKeyChar(), keyEvent.getKeyCode(),
						keyEvent.getModifiers());
			else if(keyEvent.getID() == KeyEvent.KEY_RELEASED)
				onKeyReleased(keyEvent.getKeyChar(), keyEvent.getKeyCode(),
						keyEvent.getModifiers());
		}
	}

	protected void onMousePress(int x, int y, int button) {
	}

	protected void onMouseRelease(int x, int y, int button) {
	}

	protected void onMouseEnter(int x, int y) {
	}

	protected void onMouseExit(int x, int y) {
	}

	protected void onMouseMove(int x, int y) {
	}

	protected void onMouseDrag(int x, int y) {
	}

	protected void onKeyTyped(char key, int keyCode, int modifiers) {
	}

	protected void onKeyPressed(char key, int keyCode, int modifiers) {
	}

	protected void onKeyReleased(char key, int keyCode, int modifiers) {
	}
}
