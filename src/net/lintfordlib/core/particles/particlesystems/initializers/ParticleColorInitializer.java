package net.lintfordlib.core.particles.particlesystems.initializers;

import net.lintfordlib.core.particles.Particle;

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
	public void initialize(Particle particle) {
		particle.color.r = r;
		particle.color.g = g;
		particle.color.b = b;
		particle.color.a = a;
	}
}
