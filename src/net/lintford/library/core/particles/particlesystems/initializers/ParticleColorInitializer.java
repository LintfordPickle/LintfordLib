package net.lintford.library.core.particles.particlesystems.initializers;

import net.lintford.library.core.particles.Particle;

public class ParticleColorInitializer extends ParticleInitializerBase {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 3063490433038281557L;

	public static final String INITIALIZER_NAME = "ParticleColorInitializer";

	// --------------------------------------
	// Variables
	// --------------------------------------

	public float r;
	public float g;
	public float b;
	public float a;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleColorInitializer() {
		super(INITIALIZER_NAME);

	}

	// --------------------------------------
	// Inherited-Methods
	// --------------------------------------

	@Override
	public void initialize(Particle pParticle) {
		pParticle.color.r = r;
		pParticle.color.g = g;
		pParticle.color.b = b;
		pParticle.color.a = a;

	}

}
