package net.lintford.library.core.entity;

import net.lintford.library.core.entity.instances.IPoolObjectInstance;

public abstract class PooledBaseData extends BaseInstanceData implements IPoolObjectInstance {

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

}
