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

	public float worldPositionX;
	public float worldPositionY;
	public float rotationInRadians;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void setPosition(float pWorldX, float pWorldY) {
		worldPositionX = pWorldX;
		worldPositionY = pWorldY;

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