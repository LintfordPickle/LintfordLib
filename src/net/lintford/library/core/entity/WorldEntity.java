package net.lintford.library.core.entity;

import net.lintford.library.core.entity.instances.PooledBaseData;

/**
 * The {@link WorldEntity} class allows us to order an entity spatially within the world.
 */
public abstract class WorldEntity extends PooledBaseData {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 3376631186484307065L;

	public static final int INSTANCE_UID_NOT_ASSIGNED = -1;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public transient int worldInstanceUid;

	public float worldPositionX;
	public float worldPositionY;
	public float rotationInRadians;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean isInitialized() {
		return worldInstanceUid != INSTANCE_UID_NOT_ASSIGNED;
	}

	public void setPosition(float pWorldX, float pWorldY) {
		worldPositionX = pWorldX;
		worldPositionY = pWorldY;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void init(int pWorldInstanceUid) {
		worldInstanceUid = pWorldInstanceUid;
	}

	public void reset() {
		worldInstanceUid = INSTANCE_UID_NOT_ASSIGNED;
	}
}