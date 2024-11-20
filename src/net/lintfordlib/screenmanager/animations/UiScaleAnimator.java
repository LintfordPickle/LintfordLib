package net.lintfordlib.screenmanager.animations;

import net.lintfordlib.core.maths.MathHelper;

public class UiScaleAnimator implements BaseUiAnimation {

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
		return false;
	}

	@Override
	public boolean isLooping() {
		return false;
	}

	public float normalizedRunningTime(float remainingTime) {
		final var totalRunningTime = totalRunningTimeInMs();
		return 1.f - (totalRunningTime - MathHelper.clamp(remainingTime, 0, totalRunningTime)) / totalRunningTime;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void animate(IUiAnimationTarget animationTarget, float remainingTime) {
		if (remainingTime > 0) {
			final var normalizedTime = normalizedRunningTime(remainingTime);

			final var scaleRange = Math.PI * 2 * normalizedTime;

			// rangeValue in [0,1] * magnitude
			final var rangeValue = (float) (1 - ((Math.cos(scaleRange) + 1.) * .5)) * mMagnitude;

			animationTarget.scale(1.f + rangeValue);
		} else
			animationTarget.scale(1.f);

	}
}
