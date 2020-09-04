package net.lintford.library.core.particles.particlesystems.initializers;

import net.lintford.library.core.particles.Particle;

public class ParticleRandomRotationInitializer extends ParticleSingleValueInitializer {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -208992042047147087L;

	public static final String INITIALIZER_NAME = "ParticleRandomRotationInitializer";

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleRandomRotationInitializer() {
		super(INITIALIZER_NAME);

	}

	// --------------------------------------
	// Inherited-Methods
	// --------------------------------------

	@Override
	public void onIntialiseParticle(Particle pParticle, float pValue0) {
		pParticle.rotationInRadians = pValue0;
		pParticle.dr = pValue0;

	}

}
