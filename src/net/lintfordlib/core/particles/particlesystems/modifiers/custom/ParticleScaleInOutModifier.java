package net.lintfordlib.core.particles.particlesystems.modifiers.custom;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.particles.Particle;
import net.lintfordlib.core.particles.particlesystems.modifiers.ParticleModifierBase;

public class ParticleScaleInOutModifier extends ParticleModifierBase {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -7648472396754837764L;

	public static final String MODIFIER_NAME = "ParticleScaleInOutModifier";

	// --------------------------------------
	// Variables
	// --------------------------------------

	public float minScaleAmount;
	public float maxScaleAmount;

	// --------------------------------------
	// Constants
	// --------------------------------------

	public ParticleScaleInOutModifier() {
		super(MODIFIER_NAME);

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
	public void updateParticle(LintfordCore core, Particle particle) {
		final var lNormalizedLifetime = particle.timeSinceStart / particle.lifeTime();

		// we want particles to scale in and then out, so we'll calculate the coefficient
		// to be (normalizedLifetime) * (1-normalizedLifetime).

		// (normalizedLifetime) * (1-normalizedLifetime)
		// normalizedLifetime = 0. then: (0.) * (1. - 0.) = 0
		// normalizedLifetime = 1. then: (1.) * (1. - 1.) = 0
		// normalizedLifetime = .5 then: (.5) * (1. - .5) = .25

		// since we want the maximum scale to be 1, not .25, we'll scale the
		// entire equation by 4.
		final var lScaleFactor = 4.f * lNormalizedLifetime * (1 - lNormalizedLifetime);
		particle.scale = minScaleAmount + lScaleFactor * (maxScaleAmount - minScaleAmount);
	}
}
