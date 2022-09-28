package net.lintford.library.screenmanager.transitions;

import net.lintford.library.core.LintfordCore.CoreTime;
import net.lintford.library.core.maths.MathHelper;
import net.lintford.library.core.time.TimeSpan;
import net.lintford.library.screenmanager.Screen;

public abstract class BaseTransition {

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected float mProgress;
	protected float mProgressNormalized;
	protected TimeSpan mTransitionTime;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public float progressNormalized() {
		if (mTransitionTime == null || mTransitionTime.milliseconds() == 0)
			return 0.f;
		return (float) (mProgress / mTransitionTime.milliseconds());
	}

	public boolean isFinished() {
		return mProgress >= mTransitionTime.milliseconds();
	}

	public TimeSpan timeSpan() {
		return mTransitionTime;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public BaseTransition(TimeSpan transitionTime) {
		mTransitionTime = transitionTime;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void updateTransition(Screen screen, CoreTime gameTime) {
		if (!isFinished()) {
			final var deltaTime = (float) gameTime.elapsedTimeMilli();
			mProgress += deltaTime;

			final float ms = (float) mTransitionTime.milliseconds();
			mProgressNormalized = (float) MathHelper.clamp(mProgress / ms, 0.f, 1.f);
		}
	}

	public void reset() {
		mProgress = 0;
	}

}
