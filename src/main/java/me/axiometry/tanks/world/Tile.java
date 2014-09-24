package me.axiometry.tanks.world;

import me.axiometry.tanks.rendering.Sprite;

public interface Tile {
	public short getID();

	public boolean isSolid();

	public Sprite getSprite();
}
