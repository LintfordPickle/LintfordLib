package net.lintfordlib.core.particles.particlesystems.initializers;

import net.lintfordlib.core.maths.RandomNumbers;
import net.lintfordlib.core.particles.Particle;

public abstract class ParticleRandomQuadrupleValueInitializer extends ParticleInitializerBase {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -1225598926283011057L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public float minValue0;
	public float maxValue0;
	public float minValue1;
	public float maxValue1;
	public float minValue2;
	public float maxValue2;
	public float minValue3;
	public float maxValue3;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleRandomQuadrupleValueInitializer(String definitionName) {
		super(definitionName);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public abstract void onIntialiseParticle(Particle particle, float value0, float value1, float value2, float value3);

	// @formatter:off
	@Override
	public void initialize(Particle particle) {
		onIntialiseParticle(particle, 
				getRandomValue(minValue0, maxValue0), 
				getRandomValue(minValue1, maxValue1), 
				getRandomValue(minValue2, maxValue2), 
				getRandomValue(minValue3, maxValue3));
	}

	protected float getRandomValue(float min, float max) {
		if (min == max) { 
			return min;
		} else {
			return RandomNumbers.random(min, max);
		}
	}
}
