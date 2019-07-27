package net.lintford.library.core.graphics.particles.initialisers;

import net.lintford.library.core.graphics.particles.Particle;

public class ParticleTurnToFaceInitializer implements IParticleInitializer {

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleTurnToFaceInitializer() {

	}

	// --------------------------------------
	// Inherited-Methods
	// --------------------------------------

	@Override
	public void initialize(Particle pParticle) {
		pParticle.rot = (float) Math.atan2(pParticle.dx, -pParticle.dy);

	}

}
