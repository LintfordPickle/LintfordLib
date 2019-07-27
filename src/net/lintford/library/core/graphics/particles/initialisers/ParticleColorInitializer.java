package net.lintford.library.core.graphics.particles.initialisers;

import net.lintford.library.core.graphics.particles.Particle;

public class ParticleColorInitializer implements IParticleInitializer {

	// --------------------------------------
	// Variables
	// --------------------------------------

	float mR;
	float mG;
	float mB;
	float mA;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleColorInitializer(float pR, float pG, float pB, float pA) {
		mR = pR;
		mG = pG;
		mB = pB;
		mA = pA;

	}

	// --------------------------------------
	// Inherited-Methods
	// --------------------------------------

	@Override
	public void initialize(Particle pParticle) {
		pParticle.r = mR;
		pParticle.g = mG;
		pParticle.b = mB;
		pParticle.a = mA;

	}

}
