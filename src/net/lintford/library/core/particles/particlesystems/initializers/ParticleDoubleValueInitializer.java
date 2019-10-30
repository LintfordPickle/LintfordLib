package net.lintford.library.core.particles.particlesystems.initializers;

import net.lintford.library.core.maths.RandomNumbers;
import net.lintford.library.core.particles.Particle;

public abstract class ParticleDoubleValueInitializer extends ParticleInitializerBase {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 2006777299173719891L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public float minValue0;
	public float maxValue0;
	public float minValue1;
	public float maxValue1;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleDoubleValueInitializer(final String pDefinitionName) {
		super(pDefinitionName);

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public abstract void onIntialiseParticle(final Particle pParticle, final float pValue0, final float pValue1);

	@Override
	public void initialize(Particle pParticle) {
		onIntialiseParticle(pParticle, getRandomValue(minValue0, maxValue0), getRandomValue(minValue1, maxValue1));

	}

	protected float getRandomValue(float pMin, float pMax) {
		if (pMin == pMax) {
			return pMax;

		} else {
			return RandomNumbers.random(pMin, pMax);

		}

	}

}
