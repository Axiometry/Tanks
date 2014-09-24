package me.axiometry.tanks.rendering.ui;

import me.axiometry.tanks.Tanks;
import me.axiometry.tanks.entity.Tank;
import me.axiometry.tanks.entity.singleplayer.*;
import me.axiometry.tanks.rendering.*;
import me.axiometry.tanks.rendering.components.*;
import me.axiometry.tanks.rendering.components.Button;
import me.axiometry.tanks.util.Constants;
import me.axiometry.tanks.world.World;
import me.axiometry.tanks.world.singleplayer.SinglePlayerWorld;

import java.awt.*;
import java.awt.event.InputEvent;
import java.util.Random;

public class SinglePlayerScreen implements Screen {
	private final Tanks game = Tanks.INSTANCE;
	private final Sprite background;
	private final Slider slider;
	private int scrollAmount = 0;
	private ScreenComponent[] components;

	public SinglePlayerScreen() throws Exception {
		slider = new Slider(0, 50, 0, 0, 350, 40);
		background = game.getSprites().getSpriteAt(1, 2);
		components = new ScreenComponent[] {
				new Button("Start Game", new Runnable() {
					@Override
					public void run() {
						try {
							World world = new SinglePlayerWorld();
							game.setWorld(world);
							Tank tank = new SPTank();
							tank.setX(15);
							tank.setY(15);
							world.spawnEntity(tank);
							Random random = new Random();
							for(int i = 0; i < slider.getPosition(); i++) {
								int randomX = random.nextInt(world.getWidth() - 2);
								int randomY = random.nextInt(world.getHeight() - 2);
								SPTurret sPTurret = new SPTurret();
								sPTurret.setX(0.5 + randomX);
								sPTurret.setY(0.5 + randomY);
								if(world.checkEntityCollision(sPTurret)) {
									i--;
									continue;
								} else
									world.spawnEntity(sPTurret);
							}
							world.update();
							game.setScreen(new GameScreen());
						} catch(Exception exception) {
							exception.printStackTrace();
						}
					}
				}, 0, 0, 350, 40), slider };
	}

	@Override
	public synchronized void update() {
		scrollAmount++;
		if(scrollAmount >= 64)
			scrollAmount = 0;

	}

	@Override
	public void onInputEvent(InputEvent inputEvent) {
		for(ScreenComponent component : components)
			component.onInputEvent(inputEvent);
	}

	@Override
	public synchronized void render(Graphics2D graphics, int width, int height) {
		for(int x = 0; x <= width / 32; x++)
			for(int y = 0; y <= height / 32; y++)
				graphics.drawImage(background.getImage(), (x * 32)
						- ((32 - (width % 32)) / 2), (y * 32)
						- ((64 - scrollAmount) / 2), null);
		graphics.setColor(Color.BLACK);
		Font font = Constants.BORG9.deriveFont(50.0F);
		graphics.setFont(font);
		FontMetrics metrics = graphics.getFontMetrics();
		graphics.drawString("Single Player",
				(width / 2) - (metrics.stringWidth("Single Player") / 2),
				(height / 2) - (metrics.getHeight() / 2) - 100);
		font = Constants.BORG9.deriveFont(35.0F);
		graphics.setFont(font);
		metrics = graphics.getFontMetrics();
		graphics.drawString("Difficulty:",
				(width / 2) - (metrics.stringWidth("Difficulty:") / 2),
				(height / 2) - (metrics.getHeight() / 2) - 25);

		components[0].setX((width / 2) - (components[0].getWidth() / 2));
		components[0].setY((height / 2) + 60);
		components[0].render(graphics);
		components[1].setX((width / 2) - (components[1].getWidth() / 2));
		components[1].setY((height / 2) - 20);
		components[1].render(graphics);
	}
}
