package net.lintford.library.core.graphics.particles.initialisers;

import net.lintford.library.core.graphics.particles.Particle;

public class ParticleRandomColorInitialiser extends ParticleTripleValueInitialiser {

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleRandomColorInitialiser(final float pMinValue, final float pMaxValue) {
		super(pMinValue, pMaxValue);

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
