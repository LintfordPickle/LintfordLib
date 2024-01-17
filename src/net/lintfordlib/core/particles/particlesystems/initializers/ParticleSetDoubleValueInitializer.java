package net.lintfordlib.core.particles.particlesystems.initializers;

import net.lintfordlib.core.particles.Particle;

/**
 * Initializes new {@link Particle} instance with 2 values.
 */
public abstract class ParticleSetDoubleValueInitializer extends ParticleInitializerBase {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 2006777299173719891L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public float value0;
	public float value1;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleSetDoubleValueInitializer(final String definitionName) {
		super(definitionName);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public abstract void onIntialiseParticle(final Particle particle, final float value0, final float value1);

	@Override
	public void initialize(Particle particle) {
		onIntialiseParticle(particle, value0, value1);
	}
}
