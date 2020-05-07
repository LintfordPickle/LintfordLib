package net.lintford.library.core.entity;

/**
 * The {@link WorldEntity} class allows us to order an entity spatially within the world.
 */
public abstract class WorldEntity extends PooledBaseData {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 3376631186484307065L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public float mWorldPositionX;
	public float mWorldPositionY;
	public float mRotationRadians;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void setPosition(float pWorldX, float pWorldY) {
		mWorldPositionX = pWorldX;
		mWorldPositionY = pWorldY;

	}

	public void setRotation(float pRotationInRadians) {
		mRotationRadians = pRotationInRadians;

	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public WorldEntity(final int pPoolUid) {
		super(pPoolUid);

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

}