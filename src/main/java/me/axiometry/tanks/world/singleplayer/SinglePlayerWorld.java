package me.axiometry.tanks.world.singleplayer;

import me.axiometry.tanks.Tanks;
import me.axiometry.tanks.entity.Entity;
import me.axiometry.tanks.rendering.SpriteMap;
import me.axiometry.tanks.util.IOTools;
import me.axiometry.tanks.world.*;

import java.util.*;

public final class SinglePlayerWorld extends AbstractWorld {
	private static final Entity[] emptyEntityArray = new Entity[0];

	private final List<Tile> tiles;
	private Tile[][] map;
	private int worldHeight;
	private int worldWidth;

	private final List<Entity> entities;
	private final Deque<Entity> entitiesToSpawn;

	private boolean destroyed = false;

	public SinglePlayerWorld() throws Exception {
		tiles = new ArrayList<Tile>();
		load();
		entities = new ArrayList<Entity>();
		entitiesToSpawn = new ArrayDeque<Entity>();
		Tanks.INSTANCE.getRenderer().updateWorld(this);
	}

	private void load() throws Exception {
		String data = new String(IOTools.download(getClass().getResource(
				"/world.dat")));
		SpriteMap spriteMap = Tanks.INSTANCE.getSprites();
		int definingSection = 0;
		List<Integer[]> map = new ArrayList<Integer[]>();
		tiles.clear();
		for(String line : data.split("\n")) {
			if(line.isEmpty())
				continue;
			if(line.equals("TILES") && definingSection != 1) {
				definingSection = 1;
				continue;
			}
			if(line.startsWith("MAP ") && definingSection != 2) {
				String[] spawnLocationParts = line.split(" ")[1].split(",");
				double spawnX = Double.parseDouble(spawnLocationParts[0]);
				double spawnY = Double.parseDouble(spawnLocationParts[1]);
				// spawnLocation = new Location(spawnX, spawnY);
				definingSection = 2;
				continue;
			}
			String[] parts = line.split(",");
			switch(definingSection) {
			case 1:
				short id = Short.parseShort(parts[0]);
				int spriteX = Integer.parseInt(parts[1]);
				int spriteY = Integer.parseInt(parts[2]);
				boolean solid = Boolean.valueOf(parts[3]);
				tiles.add(new SpriteTile(id, solid, spriteMap.getSpriteAt(
						spriteX, spriteY)));
				continue;
			case 2:
				Integer[] mapLine = new Integer[parts.length];
				if(map.size() > 0 && mapLine.length != map.get(0).length)
					throw new RuntimeException("Invalid map");
				for(int i = 0; i < parts.length; i++)
					mapLine[i] = Integer.valueOf(parts[i]);
				map.add(mapLine);
			}
		}
		if(map.size() == 0) {
			this.map = new Tile[0][0];
			worldHeight = 0;
			worldWidth = 0;
			return;
		}
		Tile[][] newMap = new Tile[map.size()][map.get(0).length];
		for(int i = 0; i < newMap.length; i++) {
			Integer[] ids = map.get(i);
			newMap[i] = new Tile[ids.length];
			for(int j = 0; j < ids.length; j++) {
				for(Tile tile : tiles)
					if(tile.getID() == ids[j].intValue())
						newMap[i][j] = tile;
			}
		}
		worldWidth = newMap.length;
		if(worldWidth > 0)
			worldHeight = newMap[0].length;
		else
			worldHeight = 0;
		System.out.println(worldWidth);
		System.out.println(newMap[0].length + ","
				+ newMap[newMap.length - 1].length);
		this.map = newMap;
	}

	@Override
	public synchronized void update() {
		if(destroyed)
			return;
		while(entitiesToSpawn.peek() != null)
			entities.add(entitiesToSpawn.poll());
		List<Entity> deadEntities = new ArrayList<Entity>();
		for(Entity entity : entities)
			if(entity.isDead())
				deadEntities.add(entity);
		entities.removeAll(deadEntities);
		for(Entity entity : entities) {
			entity.update();
			if(destroyed)
				return;
		}
	}

	@Override
	public Tile getTileAt(int x, int y) {
		if(x < 0 || y < 0 || x > worldWidth - 1 || y > worldHeight - 1)
			return null;
		return map[x][y];
	}

	@Override
	public int getHeight() {
		return worldHeight;
	}

	@Override
	public int getWidth() {
		return worldWidth;
	}

	@Override
	public Entity[] getEntities() {
		return entities.toArray(emptyEntityArray);
	}

	@Override
	public Entity getEntityById(int id) {
		for(Entity entity : entities)
			if(entity.getID() == id)
				return entity;
		return null;
	}

	@Override
	public boolean spawnEntity(Entity entity) {
		return entitiesToSpawn.offer(entity);
	}

	@Override
	public Tile[] getTileCache() {
		return tiles.toArray(new Tile[tiles.size()]);
	}

	@Override
	public int getTileIDAt(int x, int y) {
		Tile tile = getTileAt(x, y);
		if(tile == null)
			return 0;
		return tile.getID();
	}

	@Override
	public Tile[][] getTiles() {
		return map;
	}

	@Override
	public boolean checkEntityCollision(Entity entity) {
		boolean colliding = checkEntityToTileCollision(entity);
		for(Entity otherEntity : entities) {
			if(otherEntity.equals(entity))
				continue;
			if(colliding)
				break;
			colliding |= checkEntityToEntityCollision(entity, otherEntity);
		}
		return colliding;
	}

	@Override
	public boolean checkEntityToTileCollision(Entity entity) {
		double newX = entity.getX();
		double newY = entity.getY();
		if(newX < 0.5 || newY < 0.5 || newX > worldWidth - 0.5
				|| newY > worldHeight - 0.5)
			return true;
		int x = (int) (newX - (entity.getWidth() / 2.0));
		int y = (int) (newY - (entity.getHeight() / 2.0));
		Tile tile = getTileAt(x, y);
		if(tile == null || tile.isSolid())
			return true;

		tile = getTileAt((int) (newX + (entity.getWidth() / 2.0D)),
				(int) (newY + (entity.getHeight() / 2.0)));
		if(tile == null || tile.isSolid())
			return true;
		tile = getTileAt((int) (newX + (entity.getWidth() / 2.0D)),
				(int) (newY - (entity.getHeight() / 2.0)));
		if(tile == null || tile.isSolid())
			return true;
		tile = getTileAt((int) (newX - (entity.getWidth() / 2.0D)),
				(int) (newY + (entity.getHeight() / 2.0)));
		if(tile == null || tile.isSolid())
			return true;
		tile = getTileAt((int) (newX - (entity.getWidth() / 2.0D)),
				(int) (newY + (entity.getHeight() / 2.0)));
		if(tile == null || tile.isSolid())
			return true;
		return false;
	}

	@Override
	public boolean checkEntityToEntityCollision(Entity entity,
			Entity otherEntity) {
		if(otherEntity.getWidth() == 0 && otherEntity.getHeight() == 0)
			return false;
		Rectangle bounding1 = new Rectangle(entity.getX(), entity.getY(),
				entity.getWidth(), entity.getHeight());
		Rectangle bounding2 = new Rectangle(otherEntity.getX(),
				otherEntity.getY(), otherEntity.getWidth(),
				otherEntity.getHeight());
		if(entity.getDistanceTo(otherEntity) > Math.sqrt(Math.pow(
				bounding1.width / 2D, 2) + Math.pow(bounding2.height / 2D, 2))
				+ Math.sqrt(Math.pow(bounding2.width / 2D, 2)
						+ Math.pow(bounding2.height / 2D, 2)))
			return false;
		return checkCollision(bounding1, entity.getRotation(), bounding2,
				otherEntity.getRotation());
	}

	@Override
	public synchronized void destroy() {
		destroyed = true;
		entities.clear();
		entitiesToSpawn.clear();
		tiles.clear();
		map = null;
	}

	@Override
	public boolean isDestroyed() {
		return destroyed;
	}

	@Override
	public boolean isInitialized() {
		return true;
	}
}
