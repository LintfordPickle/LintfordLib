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

	protected transient int mInstanceUid;
	public float x;
	public float y;
	public float rotationRadians;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public int instanceUid() {
		return mInstanceUid;
	}

	public boolean isInitialized() {
		return mInstanceUid != INSTANCE_UID_NOT_ASSIGNED;
	}

	public void setPosition(float xPosition, float yPosition) {
		x = xPosition;
		y = yPosition;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void init(int instanceUid) {
		mInstanceUid = instanceUid;
	}

	public void reset() {
		mInstanceUid = INSTANCE_UID_NOT_ASSIGNED;
	}
}