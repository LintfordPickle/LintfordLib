package net.lintford.library.core.entity.instances;

import net.lintford.library.core.entity.BaseInstanceData;

public abstract class ClosedInstanceBaseData extends BaseInstanceData implements IRetainedPoolObjectInstance {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -1444873148544023277L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public final int poolUid;
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

	public ClosedInstanceBaseData(final int pPoolUid) {
		poolUid = pPoolUid;

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
