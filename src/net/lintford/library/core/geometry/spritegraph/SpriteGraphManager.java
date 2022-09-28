package net.lintford.library.core.geometry.spritegraph;

import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.entity.instances.IndexedPoolInstanceManager;
import net.lintford.library.core.geometry.spritegraph.definitions.SpriteGraphDefinition;
import net.lintford.library.core.geometry.spritegraph.instances.SpriteGraphInstance;

public class SpriteGraphManager extends IndexedPoolInstanceManager<SpriteGraphInstance> {

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

	public void loadResources(ResourceManager sesourceManager) {

	}

	public void unloadResources() {

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public SpriteGraphInstance getInstanceOfGraph(SpriteGraphDefinition spriteGraphDefinition, int entityGroupUid) {
		if (spriteGraphDefinition == null) {
			return null;
		}

		final var lSpriteGraphInstance = getFreePooledItem();
		lSpriteGraphInstance.init(spriteGraphDefinition, mSpriteGraphNodeManager, entityGroupUid);
		return lSpriteGraphInstance;
	}

	@Override
	protected SpriteGraphInstance createPoolObjectInstance() {
		return new SpriteGraphInstance(getNewPoolInstanceUid());
	}

	public void returnSpriteGraph(SpriteGraphInstance spriteGraphInstance) {
		// TODO Implement returnSpriteGraph so SpriteGraphInstances and SpriteGraphNodeInstances can be reused
	}
}