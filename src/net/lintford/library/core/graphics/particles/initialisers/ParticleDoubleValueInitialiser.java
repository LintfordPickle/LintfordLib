package net.lintford.library.core.graphics.particles.initialisers;

import net.lintford.library.core.graphics.particles.Particle;
import net.lintford.library.core.maths.RandomNumbers;

public abstract class ParticleDoubleValueInitialiser implements IParticleInitialiser {

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected float mMinValue;
	protected float mMaxValue;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleDoubleValueInitialiser(final float pMinValue, final float pMaxValue) {
		mMinValue = pMinValue;
		mMaxValue = pMaxValue;

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public abstract void onIntialiseParticle(final Particle pParticle, final float pValue0, final float pValue1);

	@Override
	public void initialise(Particle pParticle) {
		onIntialiseParticle(pParticle, getRandomValue(), getRandomValue());

	}

	protected float getRandomValue() {
		if (mMinValue == mMaxValue) {
			return mMaxValue;

		} else {
			return RandomNumbers.random(mMinValue, mMaxValue);

		}

	}

}
