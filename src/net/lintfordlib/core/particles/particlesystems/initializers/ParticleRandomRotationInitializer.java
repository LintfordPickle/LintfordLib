package net.lintfordlib.core.particles.particlesystems.initializers;

import net.lintfordlib.core.particles.Particle;

public class ParticleRandomRotationInitializer extends ParticleSingleValueInitializer {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -208992042047147087L;

	public static final String INITIALIZER_NAME = "ParticleRandomRotationInitializer";

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleRandomRotationInitializer() {
		super(INITIALIZER_NAME);

	}

	// --------------------------------------
	// Inherited-Methods
	// --------------------------------------

	@Override
	public void onIntialiseParticle(Particle particle, float rotationAmount) {
		particle.rotationInRadians = rotationAmount;
		particle.dr = rotationAmount;
	}
}
