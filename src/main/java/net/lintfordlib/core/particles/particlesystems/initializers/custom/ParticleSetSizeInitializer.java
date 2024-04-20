package net.lintfordlib.core.particles.particlesystems.initializers.custom;

import net.lintfordlib.core.particles.Particle;
import net.lintfordlib.core.particles.particlesystems.initializers.ParticleSetDoubleValueInitializer;

public class ParticleSetSizeInitializer extends ParticleSetDoubleValueInitializer {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -2507259383291058254L;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleSetSizeInitializer() {
		this(10, 10);
	}

	public ParticleSetSizeInitializer(float width, float height) {
		super(ParticleSetSizeInitializer.class.getSimpleName());

		value0 = width;
		value1 = height;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void onIntialiseParticle(Particle particle, float value, float value1) {
		particle.width = value;
		particle.height = value1;
	}
}
