package net.lintford.library.core.geometry.spritegraph;

import net.lintford.library.core.geometry.spritegraph.instance.SpriteGraphNodeInstance;

public interface ISpriteGraphPool {

	public abstract SpriteGraphNodeInstance getSpriteGraphNodeInstance();

	public abstract void returnSpriteGraphNodeInstance(SpriteGraphNodeInstance pSpriteGraphNodeInstance);

}
