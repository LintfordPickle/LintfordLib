package net.lintford.library.core.particles.particlesystems.modifiers;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.particles.Particle;

public class ParticlePhysicsModifier extends ParticleModifierBase {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 3823714289519272208L;

	public static final String MODIFIER_NAME = "ParticlePhysicsModifier";

	/** The factor of the particle vertical velocity to conserve after collisions with the floor */
	public static final float PARTICLE_FLOOR_BOUNCE_AMT = 0.5f;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticlePhysicsModifier() {
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

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void updateParticle(LintfordCore core, Particle particle) {
		final var lDelta = (float) core.appTime().elapsedTimeMilli() / 1000f;

		particle.worldPositionX += particle.dx * lDelta;
		particle.worldPositionY += particle.dy * lDelta;
		particle.rotationInRadians += Math.toRadians(particle.dr * lDelta);
	}
}
