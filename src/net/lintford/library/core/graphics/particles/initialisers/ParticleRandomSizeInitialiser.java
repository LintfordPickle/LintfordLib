package net.lintford.library.core.graphics.particles.initialisers;

import net.lintford.library.core.graphics.particles.Particle;

public class ParticleRandomSizeInitialiser extends ParticleSingleValueInitialiser {

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleRandomSizeInitialiser(float pMinValue, float pMaxValue) {
		super(pMinValue, pMaxValue);

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void onIntialiseParticle(Particle pParticle, float pValue) {
		pParticle.radius = pValue;

	}

}
