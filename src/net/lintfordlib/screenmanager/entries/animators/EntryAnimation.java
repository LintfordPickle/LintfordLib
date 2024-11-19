package net.lintfordlib.screenmanager.entries.animators;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.screenmanager.MenuEntry;

/**
 * Automates the animations for {@link MenuEntry} instances.
 */
public class EntryAnimation {

	// --------------------------------------
	// Constants
	// --------------------------------------

	// --------------------------------------
	// Variables
	// --------------------------------------

	public final MenuEntry entry;
	public float animationLength;
	public float timeRemaining;
	public Animator mAnimator;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean isAnimating() {
		return mAnimator != null && timeRemaining > 0;
	}

	public void setAnimator(Animator animator) {
		mAnimator = animator;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public EntryAnimation(MenuEntry parentEntry) {
		entry = parentEntry;
	}

	public EntryAnimation(MenuEntry parentEntry, Animator animator) {
		this(parentEntry);

		mAnimator = animator;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void update(LintfordCore core) {
		if (mAnimator == null)
			return;

		if (!isAnimating())
			return;

		timeRemaining -= core.gameTime().elapsedTimeMilli();
		mAnimator.animate(entry, timeRemaining);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void start() {
		if (mAnimator == null)
			return;

		if (isAnimating() && !mAnimator.interuptable())
			return;

		timeRemaining = mAnimator.totalRunningTimeInMs();
	}
}
