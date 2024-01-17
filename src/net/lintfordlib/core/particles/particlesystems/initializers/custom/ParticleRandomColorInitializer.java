package net.lintfordlib.core.particles.particlesystems.initializers.custom;

import net.lintfordlib.core.particles.Particle;
import net.lintfordlib.core.particles.particlesystems.initializers.ParticleRandomQuadrupleValueInitializer;

public class ParticleRandomColorInitializer extends ParticleRandomQuadrupleValueInitializer {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -1839012629735878204L;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleRandomColorInitializer() {
		this(0.f, 1.f);
	}

	public ParticleRandomColorInitializer(float min, float max) {
		this(min, max, min, max, min, max, min, max);
	}

	public ParticleRandomColorInitializer(float min0, float max0, float min1, float max1, float min2, float max2, float min3, float max3) {
		super(ParticleRandomColorInitializer.class.getSimpleName());

		minValue0 = min0;
		maxValue0 = max0;

		minValue1 = min1;
		maxValue1 = max1;

		minValue2 = min2;
		maxValue2 = max2;

		minValue3 = min3;
		maxValue3 = max3;
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
