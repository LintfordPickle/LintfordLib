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

		// TODO : This is not complete needs another constructor)

		minValue0 = offsetX;
		maxValue0 = offsetX;
		minValue1 = offsetY;
		maxValue1 = offsetY;
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
