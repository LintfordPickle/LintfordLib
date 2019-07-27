package net.lintford.library.core.graphics.particles.initializers;

import net.lintford.library.core.graphics.particles.Particle;
import net.lintford.library.core.maths.RandomNumbers;

public abstract class ParticleSingleValueInitializer implements IParticleInitializer {

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected float mMinValue;
	protected float mMaxValue;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleSingleValueInitializer(final float pMinValue, final float pMaxValue) {
		mMinValue = pMinValue;
		mMaxValue = pMaxValue;

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public abstract void onIntialiseParticle(final Particle pParticle, final float pValue);

	@Override
	public void initialize(Particle pParticle) {
		onIntialiseParticle(pParticle, getRandomValue());

	}

	protected float getRandomValue() {
		if (mMinValue == mMaxValue) {
			return mMaxValue;

		} else {
			return RandomNumbers.random(mMinValue, mMaxValue);

		}

	}

}
