package me.axiometry.tanks.rendering.ui;

import me.axiometry.tanks.Tanks;
import me.axiometry.tanks.rendering.Screen;
import me.axiometry.tanks.rendering.components.Button;
import me.axiometry.tanks.util.Constants;

import java.awt.*;
import java.awt.event.InputEvent;

public class MultiPlayerConnectErrorScreen implements Screen {
	private final Tanks game = Tanks.INSTANCE;
	private Button[] buttons;
	private String message;
	private int redOpacity = 0;
	private boolean subtractOpacity = false;

	public MultiPlayerConnectErrorScreen(String message) {
		this.message = message;
		buttons = new Button[] { new Button("Main Menu", new Runnable() {
			@Override
			public void run() {
				try {
					game.setScreen(new MainMenuScreen());
				} catch(Exception exception) {
					exception.printStackTrace();
				}
			}
		}, 0, 0, 350, 40) };
	}

	@Override
	public synchronized void update() {
		if(subtractOpacity)
			redOpacity -= 2;
		else
			redOpacity += 2;
		if(redOpacity > 100) {
			redOpacity = 100;
			subtractOpacity = true;
		} else if(redOpacity < 0) {
			redOpacity = 0;
			subtractOpacity = false;
		}
	}

	@Override
	public void onInputEvent(InputEvent inputEvent) {
		for(Button button : buttons)
			button.onInputEvent(inputEvent);
	}

	@Override
	public synchronized void render(Graphics2D graphics, int width, int height) {
		Color redTint = new Color(255, 0, 0, redOpacity);
		graphics.setColor(Color.BLACK);
		Font font = Constants.BORG9.deriveFont(50.0F);
		graphics.setFont(font);
		FontMetrics metrics = graphics.getFontMetrics();
		graphics.drawString("Error Connecting", (width / 2)
				- (metrics.stringWidth("Error Connecting") / 2), (height / 2)
				- (metrics.getHeight() / 2) - 100);
		graphics.setColor(redTint);
		graphics.drawString("Error Connecting", (width / 2)
				- (metrics.stringWidth("Error Connecting") / 2), (height / 2)
				- (metrics.getHeight() / 2) - 100);

		if(message != null) {
			graphics.setColor(Color.BLACK);
			font = Constants.BORG9.deriveFont(30.0F);
			graphics.setFont(font);
			metrics = graphics.getFontMetrics();
			graphics.drawString(message, (width / 2)
					- (metrics.stringWidth(message) / 2), (height / 2)
					- (metrics.getHeight() / 2) - 50);
			graphics.setColor(redTint);
			graphics.drawString(message, (width / 2)
					- (metrics.stringWidth(message) / 2), (height / 2)
					- (metrics.getHeight() / 2) - 50);
		}
		buttons[0].setX((width / 2) - (buttons[0].getWidth() / 2));
		buttons[0].setY((height / 2) + 60);
		buttons[0].render(graphics);
	}
}
