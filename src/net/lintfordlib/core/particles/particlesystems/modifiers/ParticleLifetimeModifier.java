package net.lintfordlib.core.particles.particlesystems.modifiers;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.particles.Particle;

public class ParticleLifetimeModifier extends ParticleModifierBase {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 2284963179665309681L;

	public static final String MODIFIER_NAME = "ParticleLifetimeAlphaFadeInOutModifier";

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleLifetimeModifier() {
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
		final var lDeltaTime = (float) core.appTime().elapsedTimeMilli();

		particle.timeSinceStart += lDeltaTime;
		if (particle.timeSinceStart >= particle.lifeTime()) {
			particle.reset();
		}
	}
}
