package net.lintfordlib.core.particles.particlesystems.initializers.custom;

import net.lintfordlib.core.particles.Particle;
import net.lintfordlib.core.particles.particlesystems.initializers.ParticleRandomDoubleValueInitializer;

public class ParticleRandomOffsetInitializer extends ParticleRandomDoubleValueInitializer {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 2635864802685231591L;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleRandomOffsetInitializer() {
		this(0.f, 0.f);
	}

	public ParticleRandomOffsetInitializer(float offsetX, float offsetY) {
		super(ParticleRandomOffsetInitializer.class.getSimpleName());

		minValue0 = offsetX;
		maxValue0 = offsetX;
		minValue1 = offsetY;
		maxValue1 = offsetY;
	}

	public ParticleRandomOffsetInitializer(float minOffsetX, float maxOffsetX, float minOffsetY, float maxOffsetY) {
		super(ParticleRandomOffsetInitializer.class.getSimpleName());

		minValue0 = minOffsetX;
		maxValue0 = maxOffsetX;
		minValue1 = minOffsetY;
		maxValue1 = maxOffsetY;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void onIntialiseParticle(Particle particle, float worldOffsetX, float worldOffsetY) {
		particle.worldPositionX += worldOffsetX;
		particle.worldPositionY += worldOffsetY;
	}
}
