package net.lintfordlib.screenmanager.animations;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.screenmanager.MenuEntry;

/**
 * Automates the animations for {@link MenuEntry} instances.
 */
public class AnimationController {

	// --------------------------------------
	// Variables
	// --------------------------------------

	public final IUiAnimationTarget entry;
	public float animationLength;
	public float timeRemaining;
	public BaseUiAnimation mAnimator;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean isAnimating() {
		return mAnimator != null && mAnimator.isLooping() || timeRemaining > 0;
	}

	public void setAnimator(BaseUiAnimation animator) {
		mAnimator = animator;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public AnimationController(IUiAnimationTarget parentEntry) {
		entry = parentEntry;
	}

	public AnimationController(IUiAnimationTarget parentEntry, BaseUiAnimation animator) {
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
		if (timeRemaining < 0 && mAnimator.isLooping())
			timeRemaining += mAnimator.totalRunningTimeInMs();

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

	public void stop() {
		if (mAnimator == null)
			return;

		if (!mAnimator.interuptable())
			return;

		timeRemaining = 0.f;
	}
}
