package net.lintfordlib.core.particles.particlesystems.initializers;

import net.lintfordlib.core.maths.RandomNumbers;
import net.lintfordlib.core.particles.Particle;

public abstract class ParticleRandomTripleValueInitializer extends ParticleInitializerBase {

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

	public ParticleRandomTripleValueInitializer(final String definitionName) {
		super(definitionName);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public abstract void onIntialiseParticle(final Particle particle, final float value0, final float value1, final float value2);

	@Override
	public void initialize(Particle particle) {
		onIntialiseParticle(particle, getRandomValue(), getRandomValue(), getRandomValue());
	}

	protected float getRandomValue() {
		if (minValue == maxValue) {
			return maxValue;
		} else {
			return RandomNumbers.random(minValue, maxValue);
		}
	}

	protected float getRandomValue(final float minValue, final float maxValue) {
		if (minValue == maxValue) {
			return maxValue;
		} else {
			return RandomNumbers.random(minValue, maxValue);
		}
	}
}
