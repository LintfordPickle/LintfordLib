package net.lintfordlib.core.particles.particlesystems.initializers;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

import net.lintfordlib.core.particles.Particle;

public abstract class ParticleInitializerBase implements Serializable {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -5277688434176646007L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	@SerializedName(value = "className")
	protected String mInitializerName;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public String name() {
		return mInitializerName;
	}

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
