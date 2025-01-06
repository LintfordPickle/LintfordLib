package net.lintfordlib.screenmanager.transitions;

import net.lintfordlib.core.LintfordCore.CoreTime;
import net.lintfordlib.core.maths.MathHelper;
import net.lintfordlib.core.time.TimeSpan;
import net.lintfordlib.screenmanager.Screen;

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

	public void timeSpan(TimeSpan newTimeSpan) {
		mTransitionTime = newTimeSpan;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	protected BaseTransition(TimeSpan transitionTime) {
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
			mProgressNormalized = MathHelper.clamp(mProgress / ms, 0.f, 1.f);
		}
	}

	public void reset() {
		mProgress = 0;
	}

	public abstract void applyFinishedEffects(Screen screen);
}
