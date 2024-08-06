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

	public void spawn(ParticleSystemInstance particleSystem, float worldX, float worldY, float zDepth, float heading, float forceX, float forceY) {
		particleSystem.spawnParticle(worldX, worldY, zDepth, forceX, forceY);
	}
}
