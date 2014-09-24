package me.axiometry.tanks.entity.multiplayer;

import me.axiometry.tanks.entity.SmokeFX;
import me.axiometry.tanks.util.IntHashMap;

import java.lang.reflect.Constructor;

public class EntityCache {
	private static final EntityCache instance;

	private final IntHashMap<Class<? extends MultiPlayerEntity>> entitiesById;

	static {
		instance = new EntityCache();
	}

	private EntityCache() {
		entitiesById = new IntHashMap<Class<? extends MultiPlayerEntity>>();
		defineEntity(MPCentralTank.class);
		defineEntity(MPOtherTank.class);
		defineEntity(MPBullet.class);
		defineEntity(MPLandmine.class);
		defineEntity(MPTurret.class);
		defineEntity(SmokeFX.class);
	}

	private void defineEntity(Class<? extends MultiPlayerEntity> entityClass) {
		if(entityClass == null)
			throw new NullPointerException("Null entity");
		Constructor<? extends MultiPlayerEntity> constructor;
		try {
			constructor = entityClass.getConstructor();
		} catch(Exception exception1) {
			throw new IllegalArgumentException("No default constructor");
		}
		MultiPlayerEntity entity;
		try {
			entity = constructor.newInstance();
		} catch(Exception exception) {
			throw new IllegalArgumentException(exception);
		}
		short netID = entity.getNetID();
		if(getEntityClass(netID) != null)
			throw new IllegalArgumentException("Duplicate packet id");
		entitiesById.put(netID, entityClass);
		entity.kill();
	}

	public Class<? extends MultiPlayerEntity> getEntityClass(short id) {
		return entitiesById.get(id);
	}

	public MultiPlayerEntity newEntity(short id) {
		try {
			return getEntityClass(id).newInstance();
		} catch(Exception exception) {}
		return null;
	}

	public static EntityCache getEntityCache() {
		return instance;
	}
}
