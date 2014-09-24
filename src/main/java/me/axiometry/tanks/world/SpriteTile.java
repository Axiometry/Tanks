package me.axiometry.tanks.world;

import me.axiometry.tanks.rendering.Sprite;

public final class SpriteTile implements Tile {
	private final short id;
	private final boolean solid;
	private final Sprite sprite;

	public SpriteTile(short id, boolean solid, Sprite sprite) {
		if(sprite == null)
			throw new NullPointerException();
		this.id = id;
		this.solid = solid;
		this.sprite = sprite;
	}

	@Override
	public short getID() {
		return id;
	}

	@Override
	public boolean isSolid() {
		return solid;
	}

	@Override
	public Sprite getSprite() {
		return sprite;
	}
}