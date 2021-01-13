package net.lintford.library.core.entity.instances;

import net.lintford.library.core.entity.BaseInstanceData;

public abstract class PooledBaseData extends BaseInstanceData {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -1444873148544023277L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public final int poolUid;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public PooledBaseData(final int pPoolUid) {
		poolUid = pPoolUid;

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void initialize(Object pObject) {

	}

	public void reset() {

	}

}
