package net.lintford.library.core.entities.instances;

import net.lintford.library.core.entities.Entity;

public abstract class ClosedPooledBaseData extends Entity {

	// --------------------------------------
	// Variables
	// --------------------------------------

	boolean internalInUse;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean isAssigned() {
		return internalInUse;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ClosedPooledBaseData(int entityUid) {
		super(entityUid);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void reset() {

	}

	public void initInstance() {
		internalInUse = true;
	}

	public void setFree() {
		reset();
		internalInUse = false;
	}
}