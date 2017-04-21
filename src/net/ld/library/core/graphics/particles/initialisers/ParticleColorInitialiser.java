package net.ld.library.core.graphics.particles.initialisers;

import net.ld.library.core.graphics.particles.Particle;

public class ParticleColorInitialiser implements IParticleInitialiser {

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

	public ParticleColorInitialiser(float pR, float pG, float pB, float pA) {
		mR = pR;
		mG = pG;
		mB = pB;
		mA = pA;

	}

	// --------------------------------------
	// Inherited-Methods
	// --------------------------------------

	@Override
	public void initialise(Particle pParticle) {
		pParticle.r = mR;
		pParticle.g = mG;
		pParticle.b = mB;
		pParticle.a = mA;

	}

}
