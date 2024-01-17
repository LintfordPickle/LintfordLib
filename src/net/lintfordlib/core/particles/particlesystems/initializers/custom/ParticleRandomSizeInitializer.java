package net.lintfordlib.core.particles.particlesystems.initializers.custom;

import net.lintfordlib.core.particles.Particle;
import net.lintfordlib.core.particles.particlesystems.initializers.ParticleRandomDoubleValueInitializer;

public class ParticleRandomSizeInitializer extends ParticleRandomDoubleValueInitializer {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -2507259383291058254L;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleRandomSizeInitializer() {
		this(0.f, 5.f);
	}

	public ParticleRandomSizeInitializer(float minSize, float maxSize) {
		this(minSize, maxSize, minSize, maxSize);
	}

	public ParticleRandomSizeInitializer(float minWidth, float maxWidth, float minHeight, float maxHeight) {
		super(ParticleRandomSizeInitializer.class.getSimpleName());
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
