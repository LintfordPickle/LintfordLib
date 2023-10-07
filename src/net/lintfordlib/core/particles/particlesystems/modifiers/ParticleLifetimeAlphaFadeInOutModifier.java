package net.lintfordlib.core.particles.particlesystems.modifiers;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.particles.Particle;

public class ParticleLifetimeAlphaFadeInOutModifier extends ParticleModifierBase {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 7071826537585613482L;

	public static final String MODIFIER_NAME = "ParticleLifetimeAlphaFadeInOutModifier";

	// --------------------------------------
	// Variables
	// --------------------------------------

	public float maxAlpha = 1.f;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleLifetimeAlphaFadeInOutModifier() {
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
	public void updateParticle(LintfordCore pore, Particle particle) {
		final var lNormalizedLifetime = particle.timeSinceStart / particle.lifeTime();
		particle.color.a = 4 * lNormalizedLifetime * (1 - lNormalizedLifetime) * maxAlpha;
	}
}
