package net.lintford.library.core.particles.particlesystems.modifiers;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.particles.Particle;

public class ParticleScaleModifier extends ParticleModifierBase {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 6192144812340882247L;

	public static final String MODIFIER_NAME = "ParticleScaleModifier";

	/** The factor of the particle vertical velocity to conserve after collisions with the floor */
	public static final float PARTICLE_FLOOR_BOUNCE_AMT = 0.5f;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public float minScaleAmount;
	public float maxScaleAmount;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleScaleModifier() {
		super(MODIFIER_NAME);

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialize(Particle particle) {
		if (maxScaleAmount <= 0.f)
			maxScaleAmount = 10.f;

		if (minScaleAmount > maxScaleAmount)
			minScaleAmount = 1.f;
	}

	@Override
	public void update(LintfordCore core) {

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void updateParticle(LintfordCore core, Particle particle) {
		final var lNormalizedLifetime = particle.timeSinceStart / particle.lifeTime();

		final var nv = (1 - lNormalizedLifetime);
		particle.scale = minScaleAmount + (maxScaleAmount - minScaleAmount) * lNormalizedLifetime;
	}
}
