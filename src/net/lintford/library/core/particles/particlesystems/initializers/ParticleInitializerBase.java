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

	protected String className;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleInitializerBase() {

	}

	public ParticleInitializerBase(final String pName) {
		className = pName;

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	/** Initializes the particle instances. */
	public abstract void initialize(Particle pParticle);

}
