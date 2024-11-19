package net.lintfordlib.screenmanager.entries.animators;

import net.lintfordlib.core.maths.MathHelper;
import net.lintfordlib.screenmanager.MenuEntry;

public class ScaleAnimator implements Animator {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private float mMagnitude = .2f;
	private float mAnimationLengthMs = 500.f;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public float magnitude() {
		return mMagnitude;
	}

	public void magnitude(float newValue) {
		mMagnitude = newValue;
	}

	public float animationLengthMs() {
		return mAnimationLengthMs;
	}

	public void animationLengthMs(float newValue) {
		mAnimationLengthMs = newValue;
	}

	@Override
	public float totalRunningTimeInMs() {
		return mAnimationLengthMs;
	}

	@Override
	public boolean interuptable() {
		return true;
	}

	public float normalizedRunningTime(float remainingTime) {
		final var totalRunningTime = totalRunningTimeInMs();
		return 1.f - (totalRunningTime - MathHelper.clamp(remainingTime, 0, totalRunningTime)) / totalRunningTime;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ScaleAnimator() {
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void animate(MenuEntry entry, float remainingTime) {
		if (remainingTime > 0) {
			final var normalizedTime = normalizedRunningTime(remainingTime);

			final var scaleRange = Math.PI * 2 * normalizedTime;

			// rangeValue in [0,1] * magnitude
			final var rangeValue = (float) (1 - ((Math.cos(scaleRange) + 1.) * .5)) * mMagnitude;

			entry.scale(1.f + rangeValue);
		} else
			entry.scale(1.f);

	}
}
