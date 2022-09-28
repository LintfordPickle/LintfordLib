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

	protected transient int mWorldInstanceUid;
	protected float mWorldPositionX;
	protected float mWorldPositionY;
	protected float mRotationInRadians;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public int worldInstanceUid() {
		return mWorldInstanceUid;
	}

	public boolean isInitialized() {
		return mWorldInstanceUid != INSTANCE_UID_NOT_ASSIGNED;
	}

	public float worldPositionX() {
		return mWorldPositionX;
	}

	public void worldPositionX(float worldPositionX) {
		mWorldPositionX = worldPositionX;
	}

	public float worldPositionY() {
		return mWorldPositionY;
	}

	public void worldPositionY(float worldPositionY) {
		mWorldPositionY = worldPositionY;
	}

	public float rotationInRadians() {
		return mRotationInRadians;
	}

	public void rotationInRadians(float rotationInRadians) {
		mRotationInRadians = rotationInRadians;
	}

	public void setPosition(float positionX, float positionY) {
		mWorldPositionX = positionX;
		mWorldPositionY = positionY;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void init(int worldInstanceUid) {
		mWorldInstanceUid = worldInstanceUid;
	}

	public void reset() {
		mWorldInstanceUid = INSTANCE_UID_NOT_ASSIGNED;
	}
}