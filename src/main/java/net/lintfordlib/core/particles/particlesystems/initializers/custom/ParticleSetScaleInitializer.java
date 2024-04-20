package net.lintfordlib.core.particles.particlesystems.initializers.custom;

import net.lintfordlib.core.particles.Particle;
import net.lintfordlib.core.particles.particlesystems.initializers.ParticleSetSingleValueInitializer;

public class ParticleSetScaleInitializer extends ParticleSetSingleValueInitializer {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -1839012629735878204L;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleSetScaleInitializer() {
		this(0.f);
	}

	public ParticleSetScaleInitializer(float scale) {
		super(ParticleSetScaleInitializer.class.getSimpleName());

		value = scale;
	}

	// --------------------------------------
	// Inherited-Methods
	// --------------------------------------

	@Override
	public void onIntialiseParticle(Particle particle, float scale) {
		particle.scale = scale;
	}
}
