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

	public TransitionSwipeOut(TimeSpan pTransitionTime, SwipeOutDirection pSwipeOutDirection) {
		super(pTransitionTime);
		mSwipeDirection = pSwipeOutDirection == SwipeOutDirection.Left ? 1.f : -1.f;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void updateTransition(Screen pScreen, CoreTime pGameTime) {
		super.updateTransition(pScreen, pGameTime);

		final var lScreenOffset = pScreen.screenPositionOffset();
		final float lScreenOffsetY = lScreenOffset.y;
		lScreenOffset.set(0.f - mProgressNormalized * mSwipeDirection * SWIPE_SPEED, lScreenOffsetY);
	}
}
