package me.axiometry.tanks.rendering.ui;

import me.axiometry.tanks.Tanks;
import me.axiometry.tanks.rendering.Screen;
import me.axiometry.tanks.rendering.components.Button;
import me.axiometry.tanks.rendering.components.TextField;
import me.axiometry.tanks.util.Constants;

import java.awt.*;
import java.awt.event.*;

public class MultiPlayerOptionsScreen implements Screen {
	private final TextField nameField = new TextField(50, 50, 300, 30);
	private final TextField addressField = new TextField(50, 50, 300, 30);
	private final Button connect;
	private final Button cancel;
	private final Color color = new Color(25, 25, 25);

	private String notification;
	private int notificationTimeout = 0;

	public MultiPlayerOptionsScreen(final MultiPlayerScreen screen) {
		connect = new Button("Connect", new Runnable() {

			@Override
			public void run() {
				if(nameField.getText().trim().isEmpty()) {
					notification = "Must enter a name";
					notificationTimeout = 40;
				} else if(addressField.getText().trim().isEmpty()) {
					notification = "Must enter an address";
					notificationTimeout = 40;
				} else {
					String address = addressField.getText().trim();
					int port = 33740;
					if(address.contains(":")) {
						String[] parts = address.split(":");
						if(parts.length != 2 || address.endsWith(":")) {
							notification = "Invalid address";
							notificationTimeout = 40;
							return;
						} else
							address = parts[0];
						try {
							port = Integer.parseInt(parts[1]);
						} catch(NumberFormatException exception) {
							notification = "Invalid port";
							notificationTimeout = 40;
							return;
						}
					}
					screen.connect(nameField.getText().trim(), address, port);
				}
			}
		}, 0, 0, 200, 40);
		cancel = new Button("Cancel", new Runnable() {

			@Override
			public void run() {
				Tanks.INSTANCE.showMainMenu();
			}
		}, 0, 0, 200, 40);
	}

	@Override
	public void onInputEvent(InputEvent inputEvent) {
		if(inputEvent instanceof KeyEvent) {
			KeyEvent event = (KeyEvent) inputEvent;
			if(event.getKeyCode() == KeyEvent.VK_TAB) {
				if(nameField.isFocused()) {
					nameField.setFocused(false);
					addressField.setFocused(true);
				} else {
					addressField.setFocused(false);
					nameField.setFocused(true);
				}
			} else if(event.getKeyCode() == KeyEvent.VK_ENTER) {
				if(nameField.isFocused()) {
					nameField.setFocused(false);
					addressField.setFocused(true);
				} else
					connect.doClick();
			} else if(event.getKeyCode() == KeyEvent.VK_ESCAPE) {
				cancel.doClick();
			}
		}
		nameField.onInputEvent(inputEvent);
		addressField.onInputEvent(inputEvent);
		connect.onInputEvent(inputEvent);
		cancel.onInputEvent(inputEvent);
	}

	@Override
	public void render(Graphics2D graphics, int width, int height) {
		String text = "MultiPlayer";
		graphics.setColor(color);
		graphics.setFont(Constants.BORG9.deriveFont(50.0F));
		graphics.drawString(
				text,
				(width / 2) - (graphics.getFontMetrics().stringWidth(text) / 2),
				((height / 2) + (graphics.getFontMetrics().getHeight() / 2) - (graphics
						.getFontMetrics().getHeight() / 4)) - 100);
		graphics.setFont(Constants.NEUROPOL_X.deriveFont(25F));
		FontMetrics metrics = graphics.getFontMetrics();
		text = "Name: ";
		graphics.drawString(
				text,
				(width / 2)
						- ((nameField.getWidth() + metrics.stringWidth(text)) / 2)
						+ 15,
				(height / 2)
						+ ((metrics.getAscent() + metrics.getDescent()) / 2)
						- 30);
		nameField.setX((width / 2)
				- ((nameField.getWidth() + metrics.stringWidth(text)) / 2)
				+ metrics.stringWidth(text) + 15);
		nameField.setY((height / 2) - (nameField.getHeight() / 2) - 30);
		nameField.render(graphics);
		graphics.setColor(color);
		String text2 = "Address: ";
		graphics.drawString(text2, (width / 2)
				- ((addressField.getWidth() + metrics.stringWidth(text)) / 2)
				- ((metrics.stringWidth(text2) - metrics.stringWidth(text)))
				+ 15,
				(height / 2)
						+ ((metrics.getAscent() + metrics.getDescent()) / 2)
						+ 10);
		addressField.setX((width / 2)
				- ((addressField.getWidth() + metrics.stringWidth(text)) / 2)
				+ metrics.stringWidth(text) + 15);
		addressField.setY((height / 2) - (addressField.getHeight() / 2) + 10);
		addressField.render(graphics);
		connect.setX((width / 2)
				- ((connect.getWidth() + cancel.getWidth() + 5) / 2));
		connect.setY((height / 2) - (connect.getHeight() / 2) + 60);
		connect.render(graphics);
		cancel.setX((width / 2)
				- ((connect.getWidth() + cancel.getWidth() + 5) / 2)
				+ connect.getWidth() + 5);
		cancel.setY((height / 2) - (cancel.getHeight() / 2) + 60);
		cancel.render(graphics);
		if(notification != null) {
			graphics.setColor(new Color(255, 0, 0, Math.min(255,
					notificationTimeout * 20)));
			graphics.setFont(Constants.NEUROPOL_X.deriveFont(25F));
			metrics = graphics.getFontMetrics();
			graphics.drawString(notification,
					(width / 2) - (metrics.stringWidth(notification) / 2),
					(height / 2) + (metrics.getHeight() / 2) + 100);
		}
	}

	@Override
	public void update() {
		nameField.update();
		addressField.update();
		connect.update();
		cancel.update();
		if(notificationTimeout > 0) {
			notificationTimeout--;
		} else if(notification != null)
			notification = null;
	}
}
