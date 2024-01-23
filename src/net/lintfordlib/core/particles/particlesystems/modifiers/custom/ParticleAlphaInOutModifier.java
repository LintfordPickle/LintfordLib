package net.lintfordlib.core.particles.particlesystems.modifiers.custom;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.particles.Particle;
import net.lintfordlib.core.particles.particlesystems.modifiers.ParticleModifierBase;

public class ParticleAlphaInOutModifier extends ParticleModifierBase {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 7071826537585613482L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public float minAlpha = 0.f;
	public float maxAlpha = 1.f;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleAlphaInOutModifier() {
		super(ParticleAlphaInOutModifier.class.getSimpleName());

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialize(Particle particle) {

	}

	@Override
	public void update(LintfordCore core) {

	}

	@Override
	public void updateParticle(LintfordCore pore, Particle particle) {
		final var lNormalizedLifetime = particle.timeSinceStart / particle.lifeTime();

		// we want particles to alpha in and then out, so we'll calculate the coefficient
		// to be (normalizedLifetime) * (1-normalizedLifetime).

		// (normalizedLifetime) * (1-normalizedLifetime)
		// normalizedLifetime = 0. then: (0.) * (1. - 0.) = 0
		// normalizedLifetime = 1. then: (1.) * (1. - 1.) = 0
		// normalizedLifetime = .5 then: (.5) * (1. - .5) = .25

		// since we want the maximum scale to be 1, not .25, we'll scale the
		// entire equation by 4.
		final var lScaleFactor = 4.f * lNormalizedLifetime * (1 - lNormalizedLifetime);
		particle.color.a = minAlpha + lScaleFactor * (maxAlpha - minAlpha);
	}
}
