package net.lintford.library.core.geometry.spritegraph;

import net.lintford.library.core.entities.instances.OpenPoolInstanceManager;
import net.lintford.library.core.geometry.spritegraph.instances.SpriteGraphNodeInstance;

public class SpriteGraphNodeManager extends OpenPoolInstanceManager<SpriteGraphNodeInstance> implements ISpriteGraphPool {

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
	public void returnSpriteGraphNodeInstance(SpriteGraphNodeInstance spriteGraphNodeInstance) {
		returnPooledItem(spriteGraphNodeInstance);
	}

	@Override
	protected SpriteGraphNodeInstance createPoolObjectInstance() {
		return new SpriteGraphNodeInstance(getNewPoolUid());
	}
}
