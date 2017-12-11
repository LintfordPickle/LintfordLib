package net.lintford.library.core.graphics.particles.modifiers;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.particles.Particle;

public class ParticleLifetimeAlphaFadeInOutModifier implements IParticleModifier {

	// --------------------------------------
	// Constants
	// --------------------------------------

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialise(Particle pParticle) {

	}

	@Override
	public void update(LintfordCore pCore) {

	}

	@Override
	public void updateParticle(LintfordCore pCore, Particle p) {
		// normalized lifetime is a value from 0 to 1 and represents how far
		// a particle is through its life. 0 means it just started, .5 is half
		// way through, and 1.0 means it's just about to be finished.
		// this value will be used to calculate alpha and scale, to avoid
		// having particles suddenly appear or disappear.
		float normalizedLifetime = p.timeSinceStart / p.lifeTime();

		// we want particles to fade in and fade out, so we'll calculate alpha
		// to be (normalizedLifetime) * (1-normalizedLifetime). this way, when
		// normalizedLifetime is 0 or 1, alpha is 0. the maximum value is at
		// normalizedLifetime = .5, and is
		// (normalizedLifetime) * (1-normalizedLifetime)
		// (.5) * (1-.5)
		// .25
		// since we want the maximum alpha to be 1, not .25, we'll scale the
		// entire equation by 4.
		p.a = 4 * normalizedLifetime * (1 - normalizedLifetime);

	}

}
