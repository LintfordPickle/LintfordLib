package net.lintford.library.core.graphics.particles.initialisers;

import net.lintford.library.core.graphics.particles.Particle;

public class ParticleTurnToFaceInitialiser implements IParticleInitialiser {

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleTurnToFaceInitialiser() {

	}

	// --------------------------------------
	// Inherited-Methods
	// --------------------------------------

	@Override
	public void initialise(Particle pParticle) {
		pParticle.rot = (float) Math.atan2(pParticle.dx, -pParticle.dy);

	}

}
