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
	public void onIntialiseParticle(Particle pParticle, float pRed, float pGreen, float pBlue) {
		pParticle.color.r = pRed;
		pParticle.color.g = pGreen;
		pParticle.color.b = pBlue;
		pParticle.color.a = 1.f;

	}

}
