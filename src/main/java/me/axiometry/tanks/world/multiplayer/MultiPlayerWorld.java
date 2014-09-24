package me.axiometry.tanks.world.multiplayer;

import me.axiometry.tanks.entity.Entity;
import me.axiometry.tanks.world.*;
import me.axiometry.tanks.world.multiplayer.protocol.bidirectional.Packet1Ping;

import java.util.*;

public final class MultiPlayerWorld extends AbstractWorld {
	private static final Entity[] emptyEntityArray = new Entity[0];

	private List<Tile> tiles;
	private Tile[][] map;
	private int worldHeight;
	private int worldWidth;

	private int ticksUntilPing = 0;

	private final NetworkManager networkManager;
	private final List<Entity> entities;
	private final Deque<Entity> entitiesToSpawn;

	private volatile boolean destroyed = false;
	private volatile boolean initialized = false;

	public MultiPlayerWorld(NetworkManager networkManager) throws Exception {
		if(networkManager == null)
			throw new NullPointerException();
		this.networkManager = networkManager;
		entities = new ArrayList<Entity>();
		entitiesToSpawn = new ArrayDeque<Entity>();
	}

	@Override
	public Tile getTileAt(int x, int y) {
		if(x < 0 || y < 0 || x > worldWidth - 1 || y > worldHeight - 1)
			return null;
		return map[x][y];
	}

	@Override
	public int getHeight() {
		if(!initialized)
			return 0;
		return worldHeight;
	}

	@Override
	public int getWidth() {
		if(!initialized)
			return 0;
		return worldWidth;
	}

	@Override
	public Entity[] getEntities() {
		if(!initialized)
			return null;
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
		if(!initialized) {
			System.out.println("Not initialized");
			return false;
		}
		return entitiesToSpawn.offer(entity);
	}

	@Override
	public Tile[] getTileCache() {
		if(!initialized)
			return null;
		return tiles.toArray(new Tile[tiles.size()]);
	}

	@Override
	public int getTileIDAt(int x, int y) {
		if(!initialized)
			return 0;
		Tile tile = getTileAt(x, y);
		if(tile == null)
			return 0;
		return tile.getID();
	}

	@Override
	public Tile[][] getTiles() {
		if(!initialized)
			return null;
		return map;
	}

	@Override
	public boolean checkEntityCollision(Entity entity) {
		if(!initialized)
			return false;
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
	public synchronized void update() {
		if(destroyed)
			return;
		networkManager.processPackets();
		if(!initialized)
			return;
		if(ticksUntilPing == 0) {
			networkManager.sendPacket(new Packet1Ping());
			ticksUntilPing = 50;
		} else
			ticksUntilPing--;
		while(!entitiesToSpawn.isEmpty())
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

	public NetworkManager getNetworkManager() {
		return networkManager;
	}

	@Override
	public synchronized void destroy() {
		if(!initialized)
			return;
		destroyed = true;
		entities.clear();
		entitiesToSpawn.clear();
		tiles.clear();
		map = null;
	}

	public synchronized void init(Tile[] tiles, Tile[][] map) {
		if(initialized)
			throw new IllegalStateException();
		initialized = true;
		this.tiles = Arrays.asList(tiles);
		this.map = map;
		worldWidth = map.length;
		if(worldWidth > 0)
			worldHeight = map[0].length;
	}

	@Override
	public boolean isDestroyed() {
		return destroyed;
	}

	@Override
	public boolean isInitialized() {
		return initialized;
	}
}
