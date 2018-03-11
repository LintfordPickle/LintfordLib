package net.lintford.library.core.graphics.sprites;

public interface AnimatedSpriteListener {

	public abstract void onStarted(SpriteInstance pSender);

	public abstract void onLooped(SpriteInstance pSender);

	public abstract void onStopped(SpriteInstance pSender);

}
