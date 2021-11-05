package net.lintford.library.core.geometry.spritegraph;

import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.entity.instances.PoolInstanceManager;
import net.lintford.library.core.geometry.spritegraph.definition.SpriteGraphDefinition;
import net.lintford.library.core.geometry.spritegraph.instance.SpriteGraphInstance;

public class SpriteGraphManager extends PoolInstanceManager<SpriteGraphInstance> {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 8130220949944972882L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private SpriteGraphNodeManager mSpriteGraphNodeManager;
	private int mPoolUidCounter;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public int getNewPoolInstanceUid() {
		return mPoolUidCounter++;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public SpriteGraphManager() {
		mSpriteGraphNodeManager = new SpriteGraphNodeManager();
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadResources(ResourceManager pResourceManager) {

	}

	public void unloadResources() {

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public SpriteGraphInstance getInstanceOfGraph(SpriteGraphDefinition pSpriteGraphDefinition, int pEntityGroupUid) {
		if (pSpriteGraphDefinition == null) {
			return null;

		}

		final SpriteGraphInstance lSpriteGraphInstance = getFreePooledItem();
		lSpriteGraphInstance.init(pSpriteGraphDefinition, mSpriteGraphNodeManager, pEntityGroupUid);
		return lSpriteGraphInstance;

	}

	@Override
	protected SpriteGraphInstance createPoolObjectInstance() {
		return new SpriteGraphInstance(getNewPoolInstanceUid());

	}

	public void returnSpriteGraph(SpriteGraphInstance lSpriteGraphInstance) {
		// TODO Implement returnSpriteGraph so SpriteGraphInstances and SpriteGraphNodeInstances can be reused

	}
}