package net.lintfordlib.screenmanager.transitions;

import net.lintfordlib.core.LintfordCore.CoreTime;
import net.lintfordlib.core.maths.MathHelper;
import net.lintfordlib.core.time.TimeSpan;
import net.lintfordlib.screenmanager.Screen;

public class TransitionFadeIn extends BaseTransition {

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public TransitionFadeIn(TimeSpan transitionTime) {
		super(transitionTime);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void updateTransition(Screen screen, CoreTime gameTime) {
		super.updateTransition(screen, gameTime);

		float alpha = MathHelper.clamp(mProgressNormalized, 0.f, 1.f);
		screen.screenColor.a = Math.min(1.f, Math.max(0.f, alpha));
	}

	@Override
	public void applyFinishedEffects(Screen screen) {
		screen.screenColor.a = 1.f;
	}
}
