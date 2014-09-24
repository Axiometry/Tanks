package me.axiometry.tanks.rendering;

import me.axiometry.tanks.world.World;

public interface GameRenderer {
	public void update();

	public void updateWorld(World world);

	public void render();

	public InputManager getInputManager();
}
