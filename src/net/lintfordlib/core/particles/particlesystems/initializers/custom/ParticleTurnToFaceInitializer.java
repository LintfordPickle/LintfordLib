package net.lintfordlib.core.particles.particlesystems.initializers.custom;

import net.lintfordlib.core.particles.Particle;
import net.lintfordlib.core.particles.particlesystems.initializers.ParticleInitializerBase;

public class ParticleTurnToFaceInitializer extends ParticleInitializerBase {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -1511765721627415767L;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleTurnToFaceInitializer() {
		super(ParticleTurnToFaceInitializer.class.getSimpleName());
	}

	// --------------------------------------
	// Inherited-Methods
	// --------------------------------------

	@Override
	public void initialize(Particle particle) {
		particle.rotationInRadians = (float) Math.atan2(particle.dx, -particle.dy);
	}
}
