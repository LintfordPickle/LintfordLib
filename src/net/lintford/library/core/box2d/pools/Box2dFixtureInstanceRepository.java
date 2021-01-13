package net.lintford.library.core.box2d.pools;

import net.lintford.library.core.box2d.instance.Box2dFixtureInstance;
import net.lintford.library.core.entity.instances.PoolInstanceManager;

public class Box2dFixtureInstanceRepository extends PoolInstanceManager<Box2dFixtureInstance> {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -6599971326126027895L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private int mPoolUidCounter;

	// --------------------------------------
	// Properties
	// --------------------------------------

	private int getNewPoolUid() {
		return mPoolUidCounter;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public Box2dFixtureInstanceRepository() {
		mPoolUidCounter = 0;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	protected Box2dFixtureInstance createPoolObjectInstance() {
		return new Box2dFixtureInstance(getNewPoolUid());
	}

}
