package net.lintfordlib.core.particles.particleemitters.shapes;

import net.lintfordlib.core.particles.particlesystems.ParticleSystemInstance;

public class ParticleEmitterPoint extends ParticleEmitterShape {

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleEmitterPoint() {
		super(EmitterType.Point);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void spawn(ParticleSystemInstance particleSystem, float worldX, float worldY, float heading, float force) {

	}
}
