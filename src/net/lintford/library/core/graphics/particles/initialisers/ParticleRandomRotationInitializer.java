package net.lintford.library.core.graphics.particles.initialisers;

import net.lintford.library.core.graphics.particles.Particle;

public class ParticleRandomRotationInitializer extends ParticleSingleValueInitializer {

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleRandomRotationInitializer(final float pMinValue, final float pMaxValue) {
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
