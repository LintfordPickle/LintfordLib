package net.lintford.library.core.entity.instances;

import net.lintford.library.core.entity.Entity;

public abstract class PooledBaseData extends Entity {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -1444873148544023277L;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public PooledBaseData(int entityUid) {
		super(entityUid);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void reset() {

	}
}