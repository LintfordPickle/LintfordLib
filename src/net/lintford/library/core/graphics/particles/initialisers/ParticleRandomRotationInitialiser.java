package net.lintford.library.core.graphics.particles.initialisers;

import net.lintford.library.core.graphics.particles.Particle;

public class ParticleRandomRotationInitialiser extends ParticleSingleValueInitialiser {

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleRandomRotationInitialiser(final float pMinValue, final float pMaxValue) {
		super(pMinValue, pMaxValue);

	}

	// --------------------------------------
	// Inherited-Methods
	// --------------------------------------

	@Override
	public void onIntialiseParticle(Particle pParticle, float pValue0) {
		pParticle.rot = pValue0;
		pParticle.dr = pValue0;

	}

}
