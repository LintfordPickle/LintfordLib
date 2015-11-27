package net.ld.library.core.particles.initializers;

import net.ld.library.core.particles.IParticleInitialiser;
import net.ld.library.core.particles.Particle;
import net.ld.library.core.time.GameTime;

public class LifeTimeInitialiser implements IParticleInitialiser {

	// =============================================
	// Variables
	// =============================================

	private float mTotalLife;

	// =============================================
	// Constructor
	// =============================================

	public LifeTimeInitialiser(float pLifetime) {
		mTotalLife = pLifetime;
	}

	// =============================================
	// Core-Methods
	// =============================================

	@Override
	public void initialise(Particle pParticle) {

		pParticle.mTotalLife = mTotalLife;
		pParticle.mLife = mTotalLife;

	}

	@Override
	public void update(Particle pParticle, GameTime pGameTime) {

	}

}
