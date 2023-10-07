package net.lintfordlib.core.particles.particlesystems.initializers;

import net.lintfordlib.core.maths.RandomNumbers;
import net.lintfordlib.core.particles.Particle;

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

	public ParticleDoubleValueInitializer(final String definitionName) {
		super(definitionName);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public abstract void onIntialiseParticle(final Particle particle, final float value0, final float value1);

	@Override
	public void initialize(Particle particle) {
		onIntialiseParticle(particle, getRandomValue(minValue0, maxValue0), getRandomValue(minValue1, maxValue1));
	}

	protected float getRandomValue(float minValue, float maxValue) {
		if (minValue == maxValue) {
			return maxValue;
		} else {
			return RandomNumbers.random(minValue, maxValue);
		}
	}
}
