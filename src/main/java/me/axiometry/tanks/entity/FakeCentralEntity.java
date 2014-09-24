package me.axiometry.tanks.entity;

import me.axiometry.tanks.rendering.*;

public class FakeCentralEntity extends AbstractEntity implements CentralEntity {

	@Override
	public Sprite getSprite() {
		return new EmptySprite();
	}

	@Override
	protected void init() {
	}

	@Override
	protected void updateEntity() {
	}
}