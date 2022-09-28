package net.lintford.library.core.particles.particlesystems.initializers;

import net.lintford.library.core.particles.Particle;

public class ParticleRandomColorInitializer extends ParticleTripleValueInitializer {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -1839012629735878204L;

	public static final String INITIALIZER_NAME = "ParticleRandomColorInitializer";

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleRandomColorInitializer() {
		super(INITIALIZER_NAME);
	}

	// --------------------------------------
	// Inherited-Methods
	// --------------------------------------

	@Override
	public void onIntialiseParticle(Particle particle, float red, float green, float blue) {
		particle.color.r = red;
		particle.color.g = green;
		particle.color.b = blue;
		particle.color.a = 1.f;
	}
}
