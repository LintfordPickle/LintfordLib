package net.lintfordlib.core.particles.particlesystems.initializers.custom;

import net.lintfordlib.core.particles.Particle;
import net.lintfordlib.core.particles.particlesystems.initializers.ParticleRandomSingleValueInitializer;

public class ParticleRandomScaleInitializer extends ParticleRandomSingleValueInitializer {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -2507259383291058254L;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleRandomScaleInitializer() {
		this(0.f, 1.f);
	}

	public ParticleRandomScaleInitializer(float minScale, float maxScale) {
		super(ParticleRandomScaleInitializer.class.getSimpleName());

		minValue = minScale;
		maxValue = maxScale;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void onIntialiseParticle(Particle particle, float value) {
		particle.scale = value;
	}
}
