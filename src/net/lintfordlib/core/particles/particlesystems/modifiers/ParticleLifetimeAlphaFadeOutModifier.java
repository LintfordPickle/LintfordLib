package net.lintfordlib.core.particles.particlesystems.modifiers;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.particles.Particle;

public class ParticleLifetimeAlphaFadeOutModifier extends ParticleModifierBase {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -5569879927420463121L;

	public static final String MODIFIER_NAME = "ParticleLifetimeAlphaFadeInOutModifier";

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleLifetimeAlphaFadeOutModifier() {
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
		particle.color.a = (1 - lNormalizedLifetime);
	}
}
