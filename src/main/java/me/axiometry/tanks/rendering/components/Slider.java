package me.axiometry.tanks.rendering.components;

import me.axiometry.tanks.rendering.AbstractScreenComponent;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class Slider extends AbstractScreenComponent {
	private static Image sliderArrow;
	private static Image sliderBar;
	private static Image sliderUnselected;
	private static Image sliderSelected;

	private boolean selected = false;
	private boolean dragging = false;

	private int startIndex;
	private int endIndex;
	private int position;

	public Slider(int startIndex, int endIndex, int x, int y, int width,
			int height) {
		super(x, y, width, height);
		if(sliderArrow == null || sliderBar == null || sliderUnselected == null
				|| sliderSelected == null) {
			BufferedImage imagesUnscaled = guiImages.getSubimage(0, 60, 20, 40);
			sliderArrow = imagesUnscaled.getSubimage(0, 0, 10, 10)
					.getScaledInstance(30, 30, 1);
			sliderBar = imagesUnscaled.getSubimage(0, 10, 10, 10)
					.getScaledInstance(30, 30, 1);
			sliderUnselected = imagesUnscaled.getSubimage(0, 20, 10, 16)
					.getScaledInstance(30, 48, 1);
			sliderSelected = imagesUnscaled.getSubimage(10, 20, 10, 16)
					.getScaledInstance(30, 48, 1);
		}
		this.startIndex = startIndex;
		this.endIndex = endIndex;
		position = Math.min(startIndex, endIndex);
	}

	@Override
	public void render(Graphics2D graphics) {
		BufferedImage image = new BufferedImage(area.width, area.height,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		g.drawImage(sliderArrow, 0,
				(area.height / 2) - (sliderArrow.getHeight(null) / 2), null);
		g.drawImage(sliderArrow, area.width,
				(area.height / 2) - (sliderArrow.getHeight(null) / 2),
				-sliderArrow.getWidth(null), sliderArrow.getHeight(null), null);
		int barWidth = area.width - (sliderArrow.getWidth(null) * 2);
		int barY = (area.height / 2) - (sliderBar.getHeight(null) / 2);
		int i = 0;
		for(; i < barWidth / sliderBar.getWidth(null); i++)
			g.drawImage(
					sliderBar,
					sliderArrow.getWidth(null) + (i * sliderBar.getWidth(null)),
					barY, null);
		int lastPartSize = barWidth % sliderBar.getWidth(null);
		if(lastPartSize > 0)
			g.drawImage(
					sliderBar,
					sliderArrow.getWidth(null) + (i * sliderBar.getWidth(null)),
					barY, lastPartSize, sliderBar.getHeight(null), null);
		Image slider = (selected ? sliderSelected : sliderUnselected);
		g.drawImage(
				slider,
				sliderArrow.getWidth(null)
						+ (int) ((barWidth - slider.getWidth(null)) * (((double) position - (double) Math
								.min(startIndex, endIndex)) / ((double) Math
								.max(startIndex, endIndex) - (double) Math.min(
								startIndex, endIndex)))), (area.height / 2)
						- (slider.getHeight(null) / 2), null);
		graphics.drawImage(image, area.x, area.y, null);
		g.setColor(Color.RED);
		g.drawRect(area.width - sliderArrow.getWidth(null), (area.height / 2)
				- (sliderArrow.getHeight(null) / 2),
				sliderArrow.getWidth(null), sliderArrow.getHeight(null));
	}

	@Override
	protected void onMouseMove(int x, int y) {
		double barWidth = area.width - (sliderArrow.getWidth(null) * 2);
		Image slider = (selected ? sliderSelected : sliderUnselected);
		if(new Rectangle(
				area.x
						+ sliderArrow.getWidth(null)
						+ (int) ((barWidth - slider.getWidth(null)) * (((double) position - (double) Math.min(
								startIndex, endIndex)) / ((double) Math.max(
								startIndex, endIndex) - (double) Math.min(
								startIndex, endIndex)))), area.y
						+ (area.height / 2) - (slider.getHeight(null) / 2),
				slider.getWidth(null), slider.getHeight(null)).contains(x, y)) {
			if(!dragging)
				selected = true;
			else
				selected = false;
		} else
			selected = false;
		if(dragging) {
			int relativePosition = x - (area.x + sliderArrow.getWidth(null));
			if(relativePosition <= 0)
				position = startIndex;
			else if(relativePosition >= barWidth)
				position = endIndex;
			else {
				int position = (int) (relativePosition / ((barWidth - (slider
						.getWidth(null) / 2.0D)) / ((double) Math.max(
						startIndex, endIndex) - (double) Math.min(startIndex,
						endIndex))));
				if(position < startIndex)
					position = startIndex;
				else if(position > endIndex)
					position = endIndex;
				if(position != this.position) {
					this.position = position;
					System.out.println(position);
				}
			}
		}
	}

	@Override
	protected void onMousePress(int x, int y, int button) {
		if(button != MouseEvent.BUTTON1)
			return;
		int barWidth = area.width - (sliderArrow.getWidth(null) * 2);
		Image slider = (selected ? sliderSelected : sliderUnselected);
		if(new Rectangle(
				area.x
						+ sliderArrow.getWidth(null)
						+ (int) ((barWidth - slider.getWidth(null)) * (((double) position - (double) Math.min(
								startIndex, endIndex)) / ((double) Math.max(
								startIndex, endIndex) - (double) Math.min(
								startIndex, endIndex)))), area.y
						+ (area.height / 2) - (slider.getHeight(null) / 2),
				slider.getWidth(null), slider.getHeight(null)).contains(x, y)) {
			dragging = true;
			selected = false;
		}
	}

	@Override
	protected void onMouseRelease(int x, int y, int button) {
		if(button != MouseEvent.BUTTON1)
			return;
		if(dragging) {
			dragging = false;
			onMouseMove(x, y);
		}
	}

	@Override
	protected void onMouseDrag(int x, int y) {
		onMouseMove(x, y);
	}

	public int getPosition() {
		return position;
	}

	public int getStartIndex() {
		return startIndex;
	}

	public int getEndIndex() {
		return endIndex;
	}
}
