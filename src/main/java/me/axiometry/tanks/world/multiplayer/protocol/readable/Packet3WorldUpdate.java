package me.axiometry.tanks.world.multiplayer.protocol.readable;

import me.axiometry.tanks.rendering.SpriteImpl;
import me.axiometry.tanks.server.io.protocol.ByteStream;
import me.axiometry.tanks.util.IntHashMap;
import me.axiometry.tanks.world.*;
import me.axiometry.tanks.world.multiplayer.*;
import me.axiometry.tanks.world.multiplayer.protocol.*;

import java.awt.Image;
import java.io.IOException;

public class Packet3WorldUpdate extends AbstractPacket implements
		ReadablePacket {
	private Tile[] tiles;
	private Tile[][] map;

	@Override
	public byte getID() {
		return 3;
	}

	@Override
	public void readData(ByteStream stream) throws IOException {
		int tileSize = readInt(stream);
		tiles = new Tile[tileSize];
		IntHashMap<Tile> tileCacheById = new IntHashMap<Tile>();
		for(int i = 0; i < tileSize; i++) {
			short id = readShort(stream);
			boolean solid = readBoolean(stream);
			Image image = readImage(stream);
			tiles[i] = new SpriteTile(id, solid, new SpriteImpl(image));
			tileCacheById.put(id, tiles[i]);
		}

		int worldWidth = readInt(stream), worldHeight = readInt(stream);
		map = new Tile[worldWidth][];
		Tile lastTile = null;
		for(int x = 0; x < worldWidth; x++) {
			map[x] = new Tile[worldHeight];
			for(int y = 0; y < worldHeight; y++) {
				byte id = stream.read();
				if(id == Byte.MAX_VALUE) {
					if(lastTile == null)
						continue;
					int skipCount = y + readShort(stream);
					for(; y < skipCount; y++)
						map[x][y] = lastTile;
					y--;
					continue;
				}
				lastTile = tileCacheById.get(id);
				map[x][y] = lastTile;
			}
		}
	}

	@Override
	public void processData(NetworkManager manager) {
		World world = game.getWorld();
		if(world == null)
			return;
		MultiPlayerWorld mpWorld = (MultiPlayerWorld) world;
		mpWorld.init(tiles, map);
	}
}
