package net.lintfordlib.core.particles.particlesystems.modifiers.custom;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.particles.Particle;
import net.lintfordlib.core.particles.particlesystems.modifiers.ParticleModifierBase;

public class ParticleScaleModifier extends ParticleModifierBase {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 6192144812340882247L;

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
		super(ParticleScaleModifier.class.getSimpleName());

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

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void updateParticle(LintfordCore core, Particle particle) {
		final var lNormalizedLifetime = particle.timeSinceStart / particle.lifeTime();
		particle.scale = minScaleAmount + (maxScaleAmount - minScaleAmount) * lNormalizedLifetime;
	}
}
