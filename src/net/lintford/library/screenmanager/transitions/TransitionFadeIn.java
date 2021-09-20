package net.lintford.library.screenmanager.transitions;

import net.lintford.library.core.LintfordCore.CoreTime;
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
	public void updateTransition(Screen pScreen, CoreTime pGameTime) {
		super.updateTransition(pScreen, pGameTime);

		float alpha = MathHelper.clamp(mProgressNormalized, 0.f, 1.f);
		pScreen.screenColor.a = Math.min(1.f, Math.max(0.f, alpha));
	}
}
