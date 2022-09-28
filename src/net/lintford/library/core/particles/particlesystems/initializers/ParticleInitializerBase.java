package net.lintford.library.core.particles.particlesystems.initializers;

import java.io.Serializable;

import net.lintford.library.core.particles.Particle;

public abstract class ParticleInitializerBase implements Serializable {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -5277688434176646007L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected String mInitializerName;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleInitializerBase() {

	}

	public ParticleInitializerBase(final String initializerName) {
		mInitializerName = initializerName;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	/** Initializes the particle instances. */
	public abstract void initialize(Particle particle);

}
