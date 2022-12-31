package net.lintford.library.core.entity;

public abstract class Entity extends BaseInstanceData {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 3376631186484307065L;

	private static int ENTITY_UID_COUNTER = 0;

	public static int getNewEntityUid() {
		return ENTITY_UID_COUNTER++;
	}

	// --------------------------------------
	// Variables
	// --------------------------------------

	public final int entityUid;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public Entity(int entityUid) {
		this.entityUid = entityUid;
	}
}