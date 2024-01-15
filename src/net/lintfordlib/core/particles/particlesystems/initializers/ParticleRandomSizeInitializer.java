package net.lintfordlib.core.particles.particlesystems.initializers;

import net.lintfordlib.core.particles.Particle;

public class ParticleRandomSizeInitializer extends ParticleDoubleValueInitializer {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -2507259383291058254L;

	public static final String INITIALIZER_NAME = "ParticleRandomSizeInitializer";

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleRandomSizeInitializer() {
		super(INITIALIZER_NAME);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void onIntialiseParticle(Particle particle, float value, float value1) {
		particle.width = value;
		particle.height = value1;
	}
}
