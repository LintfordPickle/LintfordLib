package net.lintfordlib.core.particles.particlesystems.initializers.custom;

import net.lintfordlib.core.particles.Particle;
import net.lintfordlib.core.particles.particlesystems.initializers.ParticleSetDoubleValueInitializer;

public class ParticleSetRotationInitializer extends ParticleSetDoubleValueInitializer {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -1839012629735878204L;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleSetRotationInitializer() {
		this(0.f, 0.f);
	}

	public ParticleSetRotationInitializer(float rot, float angular_velocity) {
		super(ParticleSetRotationInitializer.class.getSimpleName());

		value0 = rot;
		value1 = angular_velocity;
	}

	// --------------------------------------
	// Inherited-Methods
	// --------------------------------------

	@Override
	public void onIntialiseParticle(Particle particle, float rotation, float angular_velocity) {
		particle.rotationInRadians = rotation;
		particle.dr = angular_velocity;
	}
}
