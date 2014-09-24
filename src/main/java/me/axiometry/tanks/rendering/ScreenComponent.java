package me.axiometry.tanks.rendering;

import java.awt.Graphics2D;
import java.awt.event.InputEvent;

public interface ScreenComponent {
	public int getX();

	public int getY();

	public int getWidth();

	public int getHeight();

	public void setX(int x);

	public void setY(int y);

	public void setWidth(int width);

	public void setHeight(int height);

	public boolean contains(int x, int y);

	public void update();

	public void render(Graphics2D graphics);

	public void onInputEvent(InputEvent event);
}
