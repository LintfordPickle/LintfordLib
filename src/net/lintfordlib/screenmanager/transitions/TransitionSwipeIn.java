package net.lintfordlib.screenmanager.transitions;

import net.lintfordlib.core.LintfordCore.CoreTime;
import net.lintfordlib.core.time.TimeSpan;
import net.lintfordlib.screenmanager.Screen;

public class TransitionSwipeIn extends BaseTransition {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public enum SwipeInDirection {
		Left, Right
	}

	private static final float SWIPE_SPEED = 300.f;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private SwipeInDirection mSwipeInDirection;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public TransitionSwipeIn(TimeSpan transitionTime, SwipeInDirection swipeInDirection) {
		super(transitionTime);
		mSwipeInDirection = swipeInDirection;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void updateTransition(Screen screen, CoreTime gameTime) {
		super.updateTransition(screen, gameTime);

		final float lDirection = mSwipeInDirection == SwipeInDirection.Left ? -1.f : 1.f;
		final var lScreenOffset = screen.screenPositionOffset();
		final float lScreenOffsetY = lScreenOffset.y;
		lScreenOffset.set((-1.f + mProgressNormalized) * lDirection * SWIPE_SPEED, lScreenOffsetY);
	}
}
