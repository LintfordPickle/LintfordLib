package net.lintford.library.screenmanager.transitions;

import net.lintford.library.core.LintfordCore.CoreTime;
import net.lintford.library.core.maths.MathHelper;
import net.lintford.library.core.time.TimeSpan;
import net.lintford.library.screenmanager.Screen;

public class TransitionFadeOut extends BaseTransition {

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public TransitionFadeOut(TimeSpan pTransitionTime) {
		super(pTransitionTime);

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void updateTransition(Screen pScreen, CoreTime pGameTime) {
		super.updateTransition(pScreen, pGameTime);

		pScreen.screenColor.a = MathHelper.clamp(1.f - mProgressNormalized, 0.f, 1.f);
	}
}
