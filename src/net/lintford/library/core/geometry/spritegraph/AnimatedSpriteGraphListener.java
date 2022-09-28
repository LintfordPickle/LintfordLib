package net.lintford.library.core.geometry.spritegraph;

import net.lintford.library.core.geometry.spritegraph.instances.SpriteGraphInstance;
import net.lintford.library.core.graphics.sprites.SpriteDefinition;

public interface AnimatedSpriteGraphListener {

	public abstract void onSpriteAnimationStarted(SpriteGraphInstance spriteGraphInstance, SpriteDefinition spriteDefinition);

	public abstract void onSpriteAnimationLooped(SpriteGraphInstance spriteGraphInstance, SpriteDefinition spriteDefinition);

	public abstract void onSpriteAnimationStopped(SpriteGraphInstance spriteGraphInstance, SpriteDefinition spriteDefinition);

}
