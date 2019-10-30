package net.lintford.library.core.particles.particlesystems.initializers;

import net.lintford.library.core.maths.RandomNumbers;
import net.lintford.library.core.particles.Particle;

public abstract class ParticleTripleValueInitializer extends ParticleInitializerBase {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -1225598926283011057L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public float minValue;
	public float maxValue;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleTripleValueInitializer(final String pDefinitionName) {
		super(pDefinitionName);

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public abstract void onIntialiseParticle(final Particle pParticle, final float pValue0, final float pValue1, final float pValue2);

	@Override
	public void initialize(Particle pParticle) {
		onIntialiseParticle(pParticle, getRandomValue(), getRandomValue(), getRandomValue());

	}

	protected float getRandomValue() {
		if (minValue == maxValue) {
			return maxValue;

		} else {
			return RandomNumbers.random(minValue, maxValue);

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
