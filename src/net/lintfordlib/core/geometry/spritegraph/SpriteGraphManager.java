package net.lintfordlib.core.geometry.spritegraph;

import net.lintfordlib.core.ResourceManager;
import net.lintfordlib.core.entities.instances.OpenPoolInstanceManager;
import net.lintfordlib.core.geometry.spritegraph.definitions.SpriteGraphDefinition;
import net.lintfordlib.core.geometry.spritegraph.instances.SpriteGraphInstance;

public class SpriteGraphManager extends OpenPoolInstanceManager<SpriteGraphInstance> {

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