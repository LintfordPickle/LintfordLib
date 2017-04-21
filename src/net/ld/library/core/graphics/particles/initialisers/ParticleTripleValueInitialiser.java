package net.ld.library.core.graphics.particles.initialisers;

import net.ld.library.core.graphics.particles.Particle;
import net.ld.library.core.maths.RandomNumbers;

public abstract class ParticleTripleValueInitialiser implements IParticleInitialiser {

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected float mMinValue;
	protected float mMaxValue;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleTripleValueInitialiser(final float pMinValue, final float pMaxValue) {
		mMinValue = pMinValue;
		mMaxValue = pMaxValue;

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public abstract void onIntialiseParticle(final Particle pParticle, final float pValue0, final float pValue1, final float pValue2);

	@Override
	public void initialise(Particle pParticle) {
		onIntialiseParticle(pParticle, getRandomValue(), getRandomValue(), getRandomValue());

	}

	protected float getRandomValue() {
		if (mMinValue == mMaxValue) {
			return mMaxValue;

		} else {
			return RandomNumbers.random(mMinValue, mMaxValue);

		}

	}

	protected float getRandomValue(final float pMin, final float pMax) {
		if (pMin == pMax) {
			return pMax;

		} else {
			return RandomNumbers.random(pMin, pMax);

		}

	}

}
