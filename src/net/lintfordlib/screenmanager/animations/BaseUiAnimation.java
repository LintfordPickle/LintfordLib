package net.lintfordlib.screenmanager.animations;

public interface BaseUiAnimation {

	public abstract void animate(IUiAnimationTarget animationTarget, float remainingTime);

	/**
	 * @return The total running length of this animation, in milliseconds.
	 */
	public abstract float totalRunningTimeInMs();

	/**
	 * @return true if the animation is interuptable, otherwise false.
	 */
	public abstract boolean interuptable();

	/**
	 * @return true if this animation is a looping animation, false otherwise.
	 */
	public abstract boolean isLooping();

}
