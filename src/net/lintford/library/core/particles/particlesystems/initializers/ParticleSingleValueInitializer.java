package net.lintford.library.core.particles.particlesystems.initializers;

import net.lintford.library.core.maths.RandomNumbers;
import net.lintford.library.core.particles.Particle;

public abstract class ParticleSingleValueInitializer extends ParticleInitializerBase {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -8862569203266802997L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected float minValue;
	protected float maxValue;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleSingleValueInitializer(final String pDefinitionName) {
		super(pDefinitionName);

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
		if (minValue == maxValue) {
			return maxValue;

		} else {
			return RandomNumbers.random(minValue, maxValue);

		}

	}

}
