package net.lintfordlib.screenmanager.transitions;

import net.lintfordlib.core.LintfordCore.CoreTime;
import net.lintfordlib.core.time.TimeSpan;
import net.lintfordlib.screenmanager.Screen;

public class TransitionSwipeOut extends BaseTransition {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public enum SwipeOutDirection {
		Left, Right
	}

	private static final float SWIPE_SPEED = 1800.f;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private float mSwipeDirection;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public TransitionSwipeOut(TimeSpan transitionTime, SwipeOutDirection swipeOutDirection) {
		super(transitionTime);
		mSwipeDirection = swipeOutDirection == SwipeOutDirection.Left ? 1.f : -1.f;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void updateTransition(Screen screen, CoreTime gameTime) {
		super.updateTransition(screen, gameTime);

		final var lScreenOffset = screen.screenPositionOffset();
		final float lScreenOffsetY = lScreenOffset.y;
		lScreenOffset.set(-1.f - mProgressNormalized * mSwipeDirection * SWIPE_SPEED, lScreenOffsetY);
	}

	@Override
	public void applyFinishedEffects(Screen screen) {
		final var lScreenOffset = screen.screenPositionOffset();
		final float lScreenOffsetY = lScreenOffset.y;
		lScreenOffset.set(-1.f - 1.f * mSwipeDirection * SWIPE_SPEED, lScreenOffsetY);
	}
}
