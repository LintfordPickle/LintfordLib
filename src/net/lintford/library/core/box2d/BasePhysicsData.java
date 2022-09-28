package net.lintford.library.core.box2d;

import net.lintford.library.core.entity.instances.IndexedPooledBaseData;

public abstract class BasePhysicsData extends IndexedPooledBaseData {

	// --------------------------------------
	// Properties
	// --------------------------------------

	private static final long serialVersionUID = -4864741973346623726L;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public BasePhysicsData(int poolUid) {
		super(poolUid);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public abstract void reset();

}
