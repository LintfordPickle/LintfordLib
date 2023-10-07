package net.lintfordlib.core.particles.particlesystems.initializers;

import net.lintfordlib.core.particles.Particle;

public class ParticleRandomOffsetInitializer extends ParticleDoubleValueInitializer {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 2635864802685231591L;

	public static final String INITIALIZER_NAME = "ParticleRandomOffsetInitializer";

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleRandomOffsetInitializer() {
		super(INITIALIZER_NAME);
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
