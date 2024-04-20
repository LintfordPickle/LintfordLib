package net.lintfordlib.core.particles.particlesystems.initializers.custom;

import net.lintfordlib.core.particles.Particle;
import net.lintfordlib.core.particles.particlesystems.initializers.ParticleSetDoubleValueInitializer;

public class ParticleSetOffsetInitializer extends ParticleSetDoubleValueInitializer {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -2507259383291058254L;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleSetOffsetInitializer() {
		this(10, 10);
	}

	public ParticleSetOffsetInitializer(float offsetX, float offsetY) {
		super(ParticleSetOffsetInitializer.class.getSimpleName());

		value0 = offsetX;
		value1 = offsetY;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void onIntialiseParticle(Particle particle, float value, float value1) {
		particle.worldPositionX += value;
		particle.worldPositionY += value1;
	}
}
