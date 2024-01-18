package net.lintfordlib.core.particles.particlesystems.modifiers.custom;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.particles.Particle;
import net.lintfordlib.core.particles.particlesystems.modifiers.ParticleModifierBase;

public class ParticlePhysicsModifier extends ParticleModifierBase {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 3823714289519272208L;

	public static final String MODIFIER_NAME = "ParticlePhysicsModifier";

	/** The factor of the particle vertical velocity to conserve after collisions with the floor */
	public static final float PARTICLE_FLOOR_BOUNCE_AMT = 0.5f;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public boolean enableMovement;
	public boolean enableRotation;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticlePhysicsModifier() {
		super(MODIFIER_NAME);

		enableMovement = true;
		enableRotation = true;
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

		if (enableMovement) {
			particle.worldPositionX += particle.dx * lDelta;
			particle.worldPositionY += particle.dy * lDelta;
		}

		if (enableRotation) {
			particle.rotationInRadians += Math.toRadians(particle.dr);
		}
	}
}
