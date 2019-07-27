package net.lintford.library.core.graphics.particles.initialisers;

import net.lintford.library.core.graphics.particles.Particle;

public class ParticleRandomSizeInitializer extends ParticleSingleValueInitializer {

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleRandomSizeInitializer(float pMinValue, float pMaxValue) {
		super(pMinValue, pMaxValue);

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void onIntialiseParticle(Particle pParticle, float pValue) {
		pParticle.scale = pValue;

	}

}
