package net.lintfordlib.screenmanager.entries.animators;

import net.lintfordlib.screenmanager.MenuEntry;

public interface Animator {

	public abstract void animate(MenuEntry entry, float remainingTime);

	/**
	 * @return The total running length of this animation, in milliseconds.
	 */
	public abstract float totalRunningTimeInMs();

	/**
	 * @return true if the animation is interuptable, otherwise false.
	 */
	public abstract boolean interuptable();

}
