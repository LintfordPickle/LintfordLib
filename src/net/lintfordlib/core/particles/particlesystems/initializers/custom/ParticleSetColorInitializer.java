package net.lintfordlib.core.particles.particlesystems.initializers.custom;

import net.lintfordlib.core.particles.Particle;
import net.lintfordlib.core.particles.particlesystems.initializers.ParticleSetQuadrupleValueInitializer;

public class ParticleSetColorInitializer extends ParticleSetQuadrupleValueInitializer {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -1839012629735878204L;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleSetColorInitializer() {
		this(1.f, 1.f, 1.f, 1.f);
	}

	public ParticleSetColorInitializer(float r, float g, float b) {
		this(r, g, b, 1.f);
	}

	public ParticleSetColorInitializer(float r, float g, float b, float a) {
		super(ParticleSetColorInitializer.class.getSimpleName());

		value0 = r;
		value0 = g;
		value0 = b;
		value0 = a;
	}

	// --------------------------------------
	// Inherited-Methods
	// --------------------------------------

	@Override
	public void onIntialiseParticle(Particle particle, float red, float green, float blue, float alpha) {
		particle.color.r = red;
		particle.color.g = green;
		particle.color.b = blue;
		particle.color.a = alpha;
	}
}
