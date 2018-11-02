package net.lintford.library.screenmanager.transitions;

import net.lintford.library.core.LintfordCore.GameTime;
import net.lintford.library.core.maths.MathHelper;
import net.lintford.library.core.time.TimeSpan;
import net.lintford.library.screenmanager.Screen;

public class TransitionFadeIn extends BaseTransition {

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public TransitionFadeIn(TimeSpan pTransitionTime) {
		super(pTransitionTime);

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void updateTransition(Screen pScreen, GameTime pGameTime) {
		super.updateTransition(pScreen, pGameTime);

		final float ms = (float) mTransitionTime.milliseconds();
		final float amt = (float) (mProgress / ms);
		
		float alpha = MathHelper.clamp(amt, 0, 1);
		
		alpha = Math.min(1f, Math.max(0f, alpha));
		pScreen.color(pScreen.r(), pScreen.g(), pScreen.b(), alpha);

	}

}
