package net.lintfordlib.core.particles.particlesystems.initializers;

import net.lintfordlib.core.maths.RandomNumbers;
import net.lintfordlib.core.particles.Particle;

public abstract class ParticleRandomSingleValueInitializer extends ParticleInitializerBase {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -8862569203266802997L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public float minValue;
	public float maxValue;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleRandomSingleValueInitializer(final String definitionName) {
		super(definitionName);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public abstract void onIntialiseParticle(final Particle particle, final float value);

	@Override
	public void initialize(Particle particle) {
		onIntialiseParticle(particle, getRandomValue());
	}

	protected float getRandomValue() {
		if (minValue == maxValue) {
			return maxValue;
		} else {
			return RandomNumbers.random(minValue, maxValue);
		}
	}
}
