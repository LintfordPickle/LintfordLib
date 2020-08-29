package net.lintford.library.core.box2d;

import net.lintford.library.core.entity.PooledBaseData;

public abstract class BasePhysicsData extends PooledBaseData {

	// --------------------------------------
	// Constants
	// --------------------------------------

	// --------------------------------------
	// Properties
	// --------------------------------------

	@Override
	public boolean isAssigned() {
		return true;

	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public BasePhysicsData(int pPoolUid) {
		super(pPoolUid);

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public abstract void reset();

}
