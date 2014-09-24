package me.axiometry.tanks.entity;

import me.axiometry.tanks.Tanks;
import me.axiometry.tanks.entity.multiplayer.*;
import me.axiometry.tanks.entity.singleplayer.SinglePlayerEntity;
import me.axiometry.tanks.rendering.*;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public abstract class FXEntity extends AbstractMultiPlayerEntity implements
		SinglePlayerEntity, MultiPlayerEntity {
	protected static final SpriteMap fxSprites;

	static {
		BufferedImage image = null;
		try {
			image = new BufferedImage(32, 320, BufferedImage.TYPE_INT_ARGB);
			Graphics2D graphics = image.createGraphics();
			for(int x = 0; x < 10; x++)
				graphics.drawImage(Tanks.INSTANCE.getSprites()
						.getSpriteAt(3, x).getImage(), x * 32, 0, null);
			graphics.dispose();
		} catch(Exception exception) {
			exception.printStackTrace();
		}
		SpriteMap spriteMap = null;
		if(image != null)
			spriteMap = new SpriteMapImpl(image, 8);
		fxSprites = spriteMap;
	}

	@Override
	public int getID() {
		return -1;
	}
}
