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
		particleSystem.spawnParticle(worldX, worldY, -0.02f, 0, 0);
	}
}
