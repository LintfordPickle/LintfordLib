package net.lintfordlib.core.particles.particlesystems.initializers;

import net.lintfordlib.core.particles.Particle;

public abstract class ParticleSetTripleValueInitializer extends ParticleInitializerBase {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -1225598926283011057L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public float value0;
	public float value1;
	public float value2;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleSetTripleValueInitializer(final String definitionName) {
		super(definitionName);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public abstract void onIntialiseParticle(final Particle particle, final float value0, final float value1, final float value2);

	@Override
	public void initialize(Particle particle) {
		onIntialiseParticle(particle, value0, value1, value2);
	}
}
