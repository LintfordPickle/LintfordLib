package net.lintford.library.core.entity;

public abstract class Entity extends BaseInstanceData {

	private static int ENTITY_UID_COUNTER = 0;

	public static int getNewEntityUid() {
		return ENTITY_UID_COUNTER++;
	}

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 3376631186484307065L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public final int entityUid;
	public float x;
	public float y;
	public float rotationRadians;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void setPosition(float xPosition, float yPosition) {
		x = xPosition;
		y = yPosition;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public Entity(int entityUid) {
		this.entityUid = entityUid;
	}
}