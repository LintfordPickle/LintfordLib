package net.lintford.library.core.graphics.sprites;

public interface AnimatedSpriteListener {

	public abstract void onStarted(AnimatedSprite pSender);
	public abstract void onLooped(AnimatedSprite pSender);
	public abstract void onStopped(AnimatedSprite pSender);
	
}
