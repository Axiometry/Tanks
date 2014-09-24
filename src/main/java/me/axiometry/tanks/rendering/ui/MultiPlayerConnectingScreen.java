package me.axiometry.tanks.rendering.ui;

import me.axiometry.tanks.Tanks;
import me.axiometry.tanks.rendering.Screen;
import me.axiometry.tanks.rendering.components.*;
import me.axiometry.tanks.rendering.components.Button;
import me.axiometry.tanks.util.Constants;

import java.awt.*;
import java.awt.event.InputEvent;

public class MultiPlayerConnectingScreen implements Screen {
	private final ProgressGear gear = new ProgressGear(100, 0, 0, 120, 120);
	private final Button cancel;
	private final Color color = new Color(25, 25, 25);
	private boolean worldLoading = false;

	public MultiPlayerConnectingScreen() {
		cancel = new Button("Cancel", new Runnable() {
			@Override
			public void run() {
				Tanks.INSTANCE.showMainMenu();

			}
		}, 0, 0, 400, 40);
	}

	public void onConnect() {
		worldLoading = true;
	}

	@Override
	public void onInputEvent(InputEvent inputEvent) {
		cancel.onInputEvent(inputEvent);
	}

	@Override
	public void render(Graphics2D graphics, int width, int height) {
		String text = worldLoading ? "Loading" : "Connecting";
		graphics.setColor(color);
		graphics.setFont(Constants.BORG9.deriveFont(50.0F));
		int x = (width / 2)
				- ((gear.getWidth() + graphics.getFontMetrics().stringWidth(
						text)) / 2);
		gear.setX(x);
		gear.setY((height / 2) - (gear.getHeight() / 2));
		gear.render(graphics);
		graphics.drawString(text, x + gear.getWidth(), ((height / 2)
				+ (graphics.getFontMetrics().getHeight() / 2) - (graphics
				.getFontMetrics().getHeight() / 4)));
		cancel.setX((width / 2) - (cancel.getWidth() / 2));
		cancel.setY((height / 2) - (cancel.getHeight() / 2) + 100);
		cancel.render(graphics);
	}

	@Override
	public void update() {
		gear.update();
		cancel.update();
	}

}
