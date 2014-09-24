package me.axiometry.tanks.entity;

import me.axiometry.tanks.Tanks;
import me.axiometry.tanks.rendering.*;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class TankSprite implements Sprite {
	private final Tank tank;
	private final Sprite[] treadSprites;
	private final Sprite bodySprite;
	private final Sprite barrelSprite;
	private final Sprite dead = new EmptySprite();

	private final BufferedImage image;
	private final Graphics2D g;

	public TankSprite(Tank tank) {
		this.tank = tank;
		SpriteMap sprites = Tanks.INSTANCE.getSprites();
		treadSprites = new Sprite[] { sprites.getSpriteAt(0, 1),
				sprites.getSpriteAt(0, 2), sprites.getSpriteAt(0, 3) };
		bodySprite = sprites.getSpriteAt(0, 0);
		barrelSprite = sprites.getSpriteAt(0, 4);

		image = new BufferedImage(48, 48, BufferedImage.TYPE_INT_ARGB);
		g = image.createGraphics();
	}

	@Override
	public Image getImage() {
		if(tank.getHealth() <= 0)
			return dead.getImage();
		g.setBackground(new Color(0, 0, 0, 0));
		g.clearRect(0, 0, image.getWidth(), image.getHeight());
		Image treadImageX = treadSprites[tank.getTreadRotationX() / 10]
				.getImage();
		g.setClip((image.getWidth() - treadImageX.getWidth(null)) / 2,
				(image.getHeight() - treadImageX.getHeight(null)) / 2,
				treadImageX.getWidth(null) / 2, treadImageX.getHeight(null));
		g.drawImage(treadImageX,
				(image.getWidth() - treadImageX.getWidth(null)) / 2,
				(image.getHeight() - treadImageX.getHeight(null)) / 2, null);
		Image treadImageY = treadSprites[tank.getTreadRotationY() / 10]
				.getImage();
		g.setClip((image.getWidth() - treadImageX.getWidth(null)) / 2
				+ treadImageY.getWidth(null) / 2,
				(image.getHeight() - treadImageX.getHeight(null)) / 2,
				treadImageY.getWidth(null) / 2, treadImageY.getHeight(null));
		g.drawImage(treadImageY,
				(image.getWidth() - treadImageY.getWidth(null)) / 2,
				(image.getHeight() - treadImageY.getHeight(null)) / 2, null);
		g.setClip(null);
		Image bodyImage = bodySprite.getImage();
		g.drawImage(bodyImage,
				(image.getWidth() - bodyImage.getWidth(null)) / 2,
				(image.getHeight() - bodyImage.getHeight(null)) / 2, null);
		Image barrelImage = barrelSprite.getImage();
		AffineTransform transform = g.getTransform();
		AffineTransform newTransform = new AffineTransform(transform);
		newTransform.setToIdentity();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		newTransform.rotate(Math.toRadians(tank.getBarrelRotation()),
				image.getWidth() / 2, (image.getHeight() / 2));
		g.setTransform(newTransform);
		g.drawImage(barrelImage,
				(image.getWidth() - barrelImage.getWidth(null)) / 2,
				((image.getHeight() - barrelImage.getHeight(null)) / 2) - 4,
				null);
		g.setTransform(transform);
		return image;
	}
}