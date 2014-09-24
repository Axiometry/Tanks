package me.axiometry.tanks.rendering.ui;

import me.axiometry.tanks.Tanks;
import me.axiometry.tanks.entity.*;
import me.axiometry.tanks.rendering.*;
import me.axiometry.tanks.rendering.components.Button;
import me.axiometry.tanks.util.Constants;
import me.axiometry.tanks.world.multiplayer.MultiPlayerWorld;
import me.axiometry.tanks.world.multiplayer.protocol.writable.Packet22Respawn;

import java.awt.*;
import java.awt.event.InputEvent;

public class MultiPlayerGameOverScreen implements Screen {
	private final Tanks game = Tanks.INSTANCE;
	@SuppressWarnings("unused")
	private final Sprite background;
	private int scrollAmount = 0;
	private int redOpacity = 0;
	private boolean subtractOpacity = false;
	private Button[] buttons;
	private GameScreen gameScreen;
	private FakeCentralEntity fakeEntity;

	public MultiPlayerGameOverScreen(CentralEntity entity, GameScreen gameScreen) {
		this.gameScreen = gameScreen;
		fakeEntity = new FakeCentralEntity();
		fakeEntity.setX(entity.getX());
		fakeEntity.setY(entity.getY());
		game.getWorld().spawnEntity(fakeEntity);
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
		}, 0, 0, 250, 40), new Button("Respawn", new Runnable() {

			@Override
			public void run() {
				try {
					((MultiPlayerWorld) game.getWorld()).getNetworkManager()
							.sendPacket(new Packet22Respawn());
				} catch(Exception exception) {
					exception.printStackTrace();
				}
			}
		}, 0, 0, 250, 40) };
	}

	@Override
	public synchronized void update() {
		gameScreen.update();
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
		// for(int x = 0; x <= width / 32; x++)
		// for(int y = 0; y <= height / 32; y++)
		// graphics.drawImage(background.getImage(), (x * 32)
		// - ((32 - (width % 32)) / 2), (y * 32)
		// - ((64 - scrollAmount) / 2), null);
		gameScreen.render(graphics, width, height);
		graphics.setColor(new Color(0, 0, 0, 128));
		graphics.fillRect(0, 0, width, height);
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
		buttons[0].setX((width / 2) - buttons[0].getWidth() - 5);
		buttons[0].setY((height / 2) + 60);
		buttons[0].render(graphics);
		buttons[1].setX((width / 2) + 5);
		buttons[1].setY((height / 2) + 60);
		buttons[1].render(graphics);
	}

	public GameScreen getGameScreen() {
		return gameScreen;
	}

	public void despawnDummy() {
		fakeEntity.kill();
	}
}
