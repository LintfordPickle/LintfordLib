package net.lintford.library.core.geometry.spritegraph;

import net.lintford.library.core.entity.instances.PooledInstanceManager;
import net.lintford.library.core.geometry.spritegraph.instance.SpriteGraphNodeInstance;

public class SpriteGraphNodeManager extends PooledInstanceManager<SpriteGraphNodeInstance> implements ISpriteGraphPool {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -4965690948627392263L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private int mPoolUidCounter;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public int getNewPoolUid() {
		return mPoolUidCounter++;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public SpriteGraphNodeInstance getSpriteGraphNodeInstance() {
		return getFreePooledItem();

	}

	@Override
	public void returnSpriteGraphNodeInstance(SpriteGraphNodeInstance pSpriteGraphNodeInstance) {
		returnPooledItem(pSpriteGraphNodeInstance);

	}

	@Override
	protected SpriteGraphNodeInstance createPoolObjectInstance() {
		return new SpriteGraphNodeInstance(getNewPoolUid());

	}

}
