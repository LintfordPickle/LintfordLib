package net.lintford.library.core.particles.particlesystems.initializers;

import net.lintford.library.core.particles.Particle;

public class ParticleTurnToFaceInitializer extends ParticleInitializerBase {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -1511765721627415767L;

	public static final String INITIALIZER_NAME = "ParticleTurnToFaceInitializer";

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleTurnToFaceInitializer() {
		super(INITIALIZER_NAME);
	}

	// --------------------------------------
	// Inherited-Methods
	// --------------------------------------

	@Override
	public void initialize(Particle particle) {
		particle.rotationInRadians = (float) Math.atan2(particle.dx, -particle.dy);
	}
}
