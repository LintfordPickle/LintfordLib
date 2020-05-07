package net.lintford.library.core.particles.particlesystems.initializers;

import net.lintford.library.core.particles.Particle;

public class ParticleRandomOffsetInitializer extends ParticleDoubleValueInitializer {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 2635864802685231591L;

	public static final String INITIALIZER_NAME = "ParticleRandomOffsetInitializer";

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleRandomOffsetInitializer() {
		super(INITIALIZER_NAME);

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void onIntialiseParticle(Particle pParticle, float pValue0, float pValue1) {
		pParticle.mWorldPositionX += pValue0;
		pParticle.mWorldPositionY += pValue1;

	}

}
