package net.lintford.library.core.entity.instances;

import net.lintford.library.core.entity.BaseInstanceData;

public abstract class RetainedPooledBaseData extends BaseInstanceData implements IRetainedPoolObjectInstance {

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

	public RetainedPooledBaseData(final int pPoolUid) {
		poolUid = pPoolUid;

	}

}
