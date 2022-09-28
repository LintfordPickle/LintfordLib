package net.lintford.library.core.graphics.sprites;

public interface AnimatedSpriteListener {

	public abstract void onStarted(SpriteInstance spriteInstance);

	public abstract void onLooped(SpriteInstance spriteInstance);

	public abstract void onStopped(SpriteInstance spriteInstance);

}
