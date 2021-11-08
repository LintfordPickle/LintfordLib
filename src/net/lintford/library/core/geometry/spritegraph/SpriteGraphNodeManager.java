package net.lintford.library.core.geometry.spritegraph;

import net.lintford.library.core.entity.instances.PoolInstanceManager;
import net.lintford.library.core.geometry.spritegraph.instances.SpriteGraphNodeInstance;

public class SpriteGraphNodeManager extends PoolInstanceManager<SpriteGraphNodeInstance> implements ISpriteGraphPool {

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

	public SpriteGraphNodeManager() {

	}

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
