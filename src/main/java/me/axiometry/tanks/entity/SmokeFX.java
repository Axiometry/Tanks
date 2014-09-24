package me.axiometry.tanks.entity;

import me.axiometry.tanks.rendering.Sprite;

import java.awt.*;
import java.awt.image.BufferedImage;

public class SmokeFX extends FXEntity {
	private final SmokeSprite sprite = new SmokeSprite();
	private final double friction = 1.2D + (random.nextInt(20) / 100.0D);

	@Override
	public short getNetID() {
		return 100;
	}

	@Override
	protected void init() {
	}

	@Override
	protected void updateEntity() {
		move(friction);
		if(speedX == 0 && speedY == 0)
			kill();
	}

	private class SmokeSprite implements Sprite {
		private final Sprite smoke;

		public SmokeSprite() {
			smoke = fxSprites.getSpriteAt(0, 0);
		}

		@Override
		public Image getImage() {
			Image smokeImage = smoke.getImage();
			BufferedImage image = new BufferedImage(smokeImage.getWidth(null),
					smokeImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = image.createGraphics();
			g.setComposite(AlphaComposite.getInstance(
					AlphaComposite.SRC_OVER,
					Math.min(
							0.75f,
							Math.max(
									0.00001f,
									(float) (Math.sqrt(Math.pow(speedX, 2)
											+ Math.pow(speedY, 2))) * 10f))));
			g.drawImage(smokeImage, 0, 0, null);
			g.dispose();
			return image;
		}
	}

	@Override
	public Sprite getSprite() {
		return sprite;
	}
}
