package me.axiometry.tanks.rendering;

import java.awt.Graphics2D;
import java.awt.event.InputEvent;

public interface Screen {
	public void update();

	public void render(Graphics2D graphics, int width, int height);

	public void onInputEvent(InputEvent inputEvent);
}
