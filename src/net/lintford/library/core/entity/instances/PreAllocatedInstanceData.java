package net.lintford.library.core.entity.instances;

import net.lintford.library.core.entity.BaseInstanceData;

public abstract class PreAllocatedInstanceData extends BaseInstanceData {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 6084180705977839941L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected boolean mInternalIsAssigned;

	// --------------------------------------
	// Properties
	// --------------------------------------

	boolean internalIsAssigned() {
		return mInternalIsAssigned;
	}

	void internalIsAssigned(boolean pNewValue) {
		mInternalIsAssigned = pNewValue;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public PreAllocatedInstanceData() {

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public abstract void reset();

}
