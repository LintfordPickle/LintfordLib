package net.lintfordlib.core.particles.particleemitters.shapes;

import net.lintfordlib.core.particles.particlesystems.ParticleSystemInstance;

public abstract class ParticleEmitterShape {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public enum EmitterType {
		Point, Box, Circle, Cone, Explosion,
	}

	// --------------------------------------
	// Variables
	// --------------------------------------

	public final EmitterType emitterType;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleEmitterShape(EmitterType type) {
		emitterType = type;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public abstract void spawn(ParticleSystemInstance particleSystem, float worldX, float worldY, float heading, float force);

}
