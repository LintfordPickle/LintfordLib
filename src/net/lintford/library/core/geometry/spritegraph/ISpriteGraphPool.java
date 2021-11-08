package net.lintford.library.core.geometry.spritegraph;

import net.lintford.library.core.geometry.spritegraph.instances.SpriteGraphNodeInstance;

public interface ISpriteGraphPool {

	public abstract SpriteGraphNodeInstance getSpriteGraphNodeInstance();

	public abstract void returnSpriteGraphNodeInstance(SpriteGraphNodeInstance pSpriteGraphNodeInstance);

}
