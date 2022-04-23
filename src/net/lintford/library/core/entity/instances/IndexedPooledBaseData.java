package net.lintford.library.core.entity.instances;

import net.lintford.library.core.entity.BaseInstanceData;

public abstract class IndexedPooledBaseData extends BaseInstanceData {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -1444873148544023277L;

	public static final int NOT_ASSIGNED_UID = -1;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public final int poolUid;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public IndexedPooledBaseData(final int pPoolUid) {
		poolUid = pPoolUid;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void reset() {

	}

}