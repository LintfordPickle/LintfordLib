package net.lintfordlib.core.particles.particlesystems.initializers;

import net.lintfordlib.core.particles.Particle;

public abstract class ParticleSetQuadrupleValueInitializer extends ParticleInitializerBase {

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
	public float value3;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleSetQuadrupleValueInitializer(final String definitionName) {
		super(definitionName);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public abstract void onIntialiseParticle(final Particle particle, float value0, float value1, float value2, float value3);

	@Override
	public void initialize(Particle particle) {
		onIntialiseParticle(particle, value0, value1, value2, value3);
	}
}
