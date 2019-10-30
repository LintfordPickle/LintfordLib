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
	public void onIntialiseParticle(Particle pParticle, float pValue0, float pValue1, float pValue2) {
		pParticle.r = pValue0;
		pParticle.g = pValue1;
		pParticle.b = pValue2;

		pParticle.a = 1;

	}

}
