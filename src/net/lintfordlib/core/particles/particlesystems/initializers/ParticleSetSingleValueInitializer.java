package net.lintfordlib.core.particles.particlesystems.initializers;

import net.lintfordlib.core.particles.Particle;

public abstract class ParticleSetSingleValueInitializer extends ParticleInitializerBase {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -8862569203266802997L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public float value;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleSetSingleValueInitializer(final String definitionName) {
		super(definitionName);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public abstract void onIntialiseParticle(final Particle particle, final float value);

	@Override
	public void initialize(Particle particle) {
		onIntialiseParticle(particle, value);
	}

}
