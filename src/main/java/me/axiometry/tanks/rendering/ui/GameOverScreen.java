package me.axiometry.tanks.rendering.ui;

import me.axiometry.tanks.Tanks;
import me.axiometry.tanks.rendering.*;
import me.axiometry.tanks.rendering.components.Button;
import me.axiometry.tanks.util.Constants;

import java.awt.*;
import java.awt.event.InputEvent;

public class GameOverScreen implements Screen {
	private final Tanks game = Tanks.INSTANCE;
	private final Sprite background;
	private int scrollAmount = 0;
	private int redOpacity = 0;
	private boolean subtractOpacity = false;
	private Button[] buttons;

	public GameOverScreen() throws Exception {
		background = game.getSprites().getSpriteAt(1, 2);
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
		scrollAmount++;
		if(scrollAmount >= 64)
			scrollAmount = 0;
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
		for(int x = 0; x <= width / 32; x++)
			for(int y = 0; y <= height / 32; y++)
				graphics.drawImage(background.getImage(), (x * 32)
						- ((32 - (width % 32)) / 2), (y * 32)
						- ((64 - scrollAmount) / 2), null);
		graphics.setColor(Color.BLACK);
		Font font = Constants.BORG9.deriveFont(75.0F);
		graphics.setFont(font);
		FontMetrics metrics = graphics.getFontMetrics();
		graphics.drawString("Game Over",
				(width / 2) - (metrics.stringWidth("Game Over") / 2),
				(height / 2) - (metrics.getHeight() / 2));
		graphics.setColor(new Color(255, 0, 0, redOpacity));
		graphics.drawString("Game Over",
				(width / 2) - (metrics.stringWidth("Game Over") / 2),
				(height / 2) - (metrics.getHeight() / 2));
		buttons[0].setX((width / 2) - (buttons[0].getWidth() / 2));
		buttons[0].setY((height / 2) + 60);
		buttons[0].render(graphics);
	}
}
