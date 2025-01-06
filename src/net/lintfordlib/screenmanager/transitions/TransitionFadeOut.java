package net.lintfordlib.screenmanager.transitions;

import net.lintfordlib.core.LintfordCore.CoreTime;
import net.lintfordlib.core.maths.MathHelper;
import net.lintfordlib.core.time.TimeSpan;
import net.lintfordlib.screenmanager.Screen;

public class TransitionFadeOut extends BaseTransition {

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public TransitionFadeOut(TimeSpan transitionTime) {
		super(transitionTime);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void updateTransition(Screen screen, CoreTime gameTime) {
		super.updateTransition(screen, gameTime);

		screen.screenColor.a = MathHelper.clamp(1.f - mProgressNormalized, 0.f, 1.f);
	}

	@Override
	public void applyFinishedEffects(Screen screen) {
		screen.screenColor.a = 0.f;

	}
}
