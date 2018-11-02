package net.lintford.library.screenmanager.transitions;

import net.lintford.library.core.LintfordCore.GameTime;
import net.lintford.library.core.maths.MathHelper;
import net.lintford.library.core.time.TimeSpan;
import net.lintford.library.screenmanager.Screen;

public class TransitionFadeOut extends BaseTransition {

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public TransitionFadeOut(TimeSpan pTransitionTime) {
		super(pTransitionTime);
		// TODO Auto-generated constructor stub
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void updateTransition(Screen pScreen, GameTime pGameTime) {
		super.updateTransition(pScreen, pGameTime);

		final float ms = (float) mTransitionTime.milliseconds();
		final float amt = (float) (mProgress / ms);

		float alpha = MathHelper.clamp(1 - amt, 0, 1);

		pScreen.color(pScreen.r(), pScreen.g(), pScreen.b(), alpha);

	}

}
