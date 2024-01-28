package net.lintfordlib.core.particles.particleemitters.shapes;

import net.lintfordlib.core.particles.particlesystems.ParticleSystemInstance;

public class ParticleEmitterPoint extends ParticleEmitterShape {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -5807058157231350398L;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleEmitterPoint() {
		super(ParticleEmitterPoint.class.getSimpleName());
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void spawn(ParticleSystemInstance particleSystem, float worldX, float worldY, float heading, float force) {
		final var lVelX = (float) Math.cos(heading) * force;
		final var lVelY = (float) Math.sin(heading) * force;

		particleSystem.spawnParticle(worldX, worldY, -0.02f, lVelX, lVelY);
	}
}
