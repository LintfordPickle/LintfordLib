package net.lintford.library.core.entities.instances;

import net.lintford.library.core.entities.Entity;

public abstract class ClosedInstanceBaseData extends Entity {

	// --------------------------------------
	// Variables
	// --------------------------------------

	boolean internalInUse; // used by the ClosedInstanceManager to track the assigned state of this instance

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean isAssigned() {
		return internalInUse;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ClosedInstanceBaseData(int entityUid) {
		super(entityUid);

		reset();
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
