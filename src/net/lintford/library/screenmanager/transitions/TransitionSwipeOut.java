package net.lintford.library.screenmanager.transitions;

import net.lintford.library.core.LintfordCore.CoreTime;
import net.lintford.library.core.time.TimeSpan;
import net.lintford.library.screenmanager.Screen;

public class TransitionSwipeOut extends BaseTransition {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public enum SwipeOutDirection {
		Left, Right
	}

	private static final float SWIPE_SPEED = 300.f;

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
		lScreenOffset.set(0.f - mProgressNormalized * mSwipeDirection * SWIPE_SPEED, lScreenOffsetY);
	}
}
