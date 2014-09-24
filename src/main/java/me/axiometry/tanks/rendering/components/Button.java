package me.axiometry.tanks.rendering.components;

import me.axiometry.tanks.rendering.AbstractScreenComponent;
import me.axiometry.tanks.util.Constants;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class Button extends AbstractScreenComponent {
	private static Image[] buttonUnselected;
	private static Image[] buttonSelected;
	private static Image[] buttonPressed;

	private static final int IMAGE_TOP_LEFT = 0;
	private static final int IMAGE_TOP_MIDDLE = 3;
	private static final int IMAGE_TOP_RIGHT = 4;
	private static final int IMAGE_CENTER_LEFT = 1;
	private static final int IMAGE_CENTER_MIDDLE = 5;
	private static final int IMAGE_CENTER_RIGHT = 6;
	private static final int IMAGE_BOTTOM_LEFT = 2;
	private static final int IMAGE_BOTTOM_MIDDLE = 8;
	private static final int IMAGE_BOTTOM_RIGHT = 7;

	private Runnable pressAction;
	private String text;
	private boolean selected = false;
	private boolean pressed = false;

	public Button(String text, Runnable pressAction, int x, int y, int width,
			int height) {
		super(x, y, width, height);
		if(buttonUnselected == null || buttonSelected == null) {
			BufferedImage buttonsUnscaled = guiImages.getSubimage(0, 0, 20, 60);
			buttonUnselected = new Image[] {
					buttonsUnscaled.getSubimage(0, 0, 5, 5),
					buttonsUnscaled.getSubimage(0, 5, 5, 10),
					buttonsUnscaled.getSubimage(0, 15, 5, 5),
					buttonsUnscaled.getSubimage(5, 0, 10, 5),
					buttonsUnscaled.getSubimage(15, 0, 5, 5),
					buttonsUnscaled.getSubimage(5, 5, 10, 10),
					buttonsUnscaled.getSubimage(15, 5, 5, 10),
					buttonsUnscaled.getSubimage(15, 15, 5, 5),
					buttonsUnscaled.getSubimage(5, 15, 10, 5) };
			buttonSelected = new Image[] {
					buttonsUnscaled.getSubimage(0, 20, 5, 5),
					buttonsUnscaled.getSubimage(0, 25, 5, 10),
					buttonsUnscaled.getSubimage(0, 35, 5, 5),
					buttonsUnscaled.getSubimage(5, 20, 10, 5),
					buttonsUnscaled.getSubimage(15, 20, 5, 5),
					buttonsUnscaled.getSubimage(5, 25, 10, 10),
					buttonsUnscaled.getSubimage(15, 25, 5, 10),
					buttonsUnscaled.getSubimage(15, 35, 5, 5),
					buttonsUnscaled.getSubimage(5, 35, 10, 5) };
			buttonPressed = new Image[] {
					buttonsUnscaled.getSubimage(0, 40, 5, 5),
					buttonsUnscaled.getSubimage(0, 45, 5, 10),
					buttonsUnscaled.getSubimage(0, 55, 5, 5),
					buttonsUnscaled.getSubimage(5, 40, 10, 5),
					buttonsUnscaled.getSubimage(15, 40, 5, 5),
					buttonsUnscaled.getSubimage(5, 45, 10, 10),
					buttonsUnscaled.getSubimage(15, 45, 5, 10),
					buttonsUnscaled.getSubimage(15, 55, 5, 5),
					buttonsUnscaled.getSubimage(5, 55, 10, 5) };
			Image[][] buttonImages = new Image[][] { buttonUnselected,
					buttonSelected, buttonPressed };
			for(Image[] button : buttonImages)
				for(int i = 0; i < button.length; i++)
					button[i] = button[i].getScaledInstance(
							button[i].getWidth(null) * 2,
							button[i].getHeight(null) * 2, 1);
		}
		this.text = text;
		this.pressAction = pressAction;
	}

	@Override
	public void render(Graphics2D graphics) {
		BufferedImage image = new BufferedImage(area.width, area.height,
				BufferedImage.TYPE_INT_ARGB);
		Image[] buttonImages = (pressed ? buttonPressed
				: (selected ? buttonSelected : buttonUnselected));
		Graphics2D g = image.createGraphics();
		g.drawImage(buttonImages[IMAGE_TOP_LEFT], 0, 0, null);
		int separatorHeight = (area.height - (buttonImages[IMAGE_TOP_LEFT]
				.getHeight(null) + buttonImages[IMAGE_BOTTOM_LEFT]
				.getHeight(null)));
		g.drawImage(buttonImages[IMAGE_CENTER_LEFT], 0,
				buttonImages[IMAGE_TOP_LEFT].getHeight(null),
				buttonImages[IMAGE_CENTER_LEFT].getWidth(null),
				separatorHeight, null);
		g.drawImage(buttonImages[IMAGE_BOTTOM_LEFT], 0,
				buttonImages[IMAGE_TOP_LEFT].getHeight(null) + separatorHeight,
				null);
		int separatorWidth = (area.width - (buttonImages[IMAGE_TOP_LEFT]
				.getHeight(null) + buttonImages[IMAGE_TOP_RIGHT]
				.getHeight(null)));
		g.drawImage(buttonImages[IMAGE_TOP_MIDDLE],
				buttonImages[IMAGE_TOP_LEFT].getWidth(null), 0, separatorWidth,
				buttonImages[IMAGE_TOP_MIDDLE].getHeight(null), null);
		g.drawImage(buttonImages[IMAGE_TOP_RIGHT],
				buttonImages[IMAGE_TOP_LEFT].getWidth(null) + separatorWidth,
				0, null);
		g.drawImage(
				buttonImages[IMAGE_CENTER_RIGHT],
				buttonImages[IMAGE_CENTER_LEFT].getWidth(null) + separatorWidth,
				buttonImages[IMAGE_TOP_RIGHT].getHeight(null),
				buttonImages[IMAGE_CENTER_RIGHT].getWidth(null),
				separatorHeight, null);
		g.drawImage(buttonImages[IMAGE_BOTTOM_MIDDLE],
				buttonImages[IMAGE_BOTTOM_LEFT].getWidth(null),
				buttonImages[IMAGE_TOP_MIDDLE].getHeight(null)
						+ separatorHeight, separatorWidth,
				buttonImages[IMAGE_BOTTOM_MIDDLE].getHeight(null), null);
		g.drawImage(
				buttonImages[IMAGE_BOTTOM_RIGHT],
				buttonImages[IMAGE_BOTTOM_LEFT].getWidth(null) + separatorWidth,
				buttonImages[IMAGE_TOP_RIGHT].getHeight(null) + separatorHeight,
				null);
		g.drawImage(buttonImages[IMAGE_CENTER_MIDDLE],
				buttonImages[IMAGE_TOP_LEFT].getWidth(null),
				buttonImages[IMAGE_TOP_LEFT].getHeight(null), separatorWidth,
				separatorHeight, null);
		graphics.drawImage(image, area.x, area.y, null);
		graphics.setFont(Constants.BORG9.deriveFont(35.0F));
		graphics.setColor(Color.BLACK);
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.drawString(
				text,
				area.x
						+ ((area.width / 2) - (graphics.getFontMetrics()
								.stringWidth(text) / 2)),
				area.y
						+ ((area.height / 2)
								+ (graphics.getFontMetrics().getHeight() / 2) - (graphics
								.getFontMetrics().getHeight() / 4)));
	}

	@Override
	protected void onMousePress(int x, int y, int button) {
		if(button != MouseEvent.BUTTON1)
			return;
		pressed = true;
	}

	@Override
	protected void onMouseRelease(int x, int y, int button) {
		if(button != MouseEvent.BUTTON1)
			return;
		pressed = false;
		doClick();
	}

	public void doClick() {
		pressAction.run();
	}

	@Override
	protected void onMouseEnter(int x, int y) {
		selected = true;
	}

	@Override
	protected void onMouseExit(int x, int y) {
		selected = false;
	}
}
