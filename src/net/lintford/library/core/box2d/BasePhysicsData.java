package net.lintford.library.core.box2d;

import net.lintford.library.core.entity.instances.PooledBaseData;

public abstract class BasePhysicsData extends PooledBaseData {

	// --------------------------------------
	// Properties
	// --------------------------------------

	private static final long serialVersionUID = -4864741973346623726L;

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
