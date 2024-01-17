package net.lintfordlib.core.particles.particlesystems.initializers.custom;

import net.lintfordlib.core.particles.Particle;
import net.lintfordlib.core.particles.particlesystems.initializers.ParticleSetSingleValueInitializer;

public class ParticleSetRotationInitializer extends ParticleSetSingleValueInitializer {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -1839012629735878204L;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleSetRotationInitializer() {
		this(0.f);
	}

	public ParticleSetRotationInitializer(float rot) {
		super(ParticleSetRotationInitializer.class.getSimpleName());

		value = rot;
	}

	// --------------------------------------
	// Inherited-Methods
	// --------------------------------------

	@Override
	public void onIntialiseParticle(Particle particle, float rotation) {
		particle.rotationInRadians = rotation;
	}
}
