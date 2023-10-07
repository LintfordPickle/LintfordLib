package net.lintfordlib.core.entities.instances;

import net.lintfordlib.core.entities.Entity;

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
