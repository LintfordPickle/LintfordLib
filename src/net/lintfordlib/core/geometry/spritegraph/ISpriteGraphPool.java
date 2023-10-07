package net.lintfordlib.core.geometry.spritegraph;

import net.lintfordlib.core.geometry.spritegraph.instances.SpriteGraphNodeInstance;

public interface ISpriteGraphPool {

	public abstract SpriteGraphNodeInstance getSpriteGraphNodeInstance();

	public abstract void returnSpriteGraphNodeInstance(SpriteGraphNodeInstance spriteGraphNodeInstance);

}
