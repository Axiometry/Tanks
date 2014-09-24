package me.axiometry.tanks.world;

import me.axiometry.tanks.entity.Entity;

public interface World {
	public void update();

	public Tile[][] getTiles();

	public Tile getTileAt(int x, int y);

	public int getTileIDAt(int x, int y);

	public int getWidth();

	public int getHeight();

	public Entity[] getEntities();

	public Entity getEntityById(int id);

	public Tile[] getTileCache();

	public boolean spawnEntity(Entity entity);

	public boolean checkEntityCollision(Entity entity);

	public boolean checkEntityToTileCollision(Entity entity);

	public boolean checkEntityToEntityCollision(Entity entity,
			Entity otherEntity);

	public void destroy();

	public boolean isDestroyed();

	public boolean isInitialized();
}
