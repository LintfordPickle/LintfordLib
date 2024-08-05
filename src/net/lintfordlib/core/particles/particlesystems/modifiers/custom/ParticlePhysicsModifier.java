package net.lintfordlib.core.particles.particlesystems.modifiers.custom;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.particles.Particle;
import net.lintfordlib.core.particles.particlesystems.modifiers.ParticleModifierBase;

public class ParticlePhysicsModifier extends ParticleModifierBase {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 3823714289519272208L;

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
		super(ParticlePhysicsModifier.class.getSimpleName());

		enableMovement = true;
		enableRotation = true;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialize(Particle particle) {
		// ignore
	}

	@Override
	public void update(LintfordCore core) {
		// ignore
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void updateParticle(LintfordCore core, Particle particle) {
		final var dt = (float) core.appTime().elapsedTimeMilli() / 1000f;

		if (enableMovement) {
			particle.worldPositionX += particle.vx * dt;
			particle.worldPositionY += particle.vy * dt;
		}

		if (enableRotation) {
			particle.rotationInRadians += Math.toRadians(particle.angVel);
		}
	}
}
