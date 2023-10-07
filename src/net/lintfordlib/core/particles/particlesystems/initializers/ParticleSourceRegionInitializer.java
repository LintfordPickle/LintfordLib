package net.lintfordlib.core.particles.particlesystems.initializers;

import net.lintfordlib.core.particles.Particle;

/**
 * Sets the initial source region of a {@link Particle} when it is spawned in the {@link SimpleAnimationRenderer}. This determines which area of the texture the particle takes the
 * image from.
 */
public class ParticleSourceRegionInitializer extends ParticleInitializerBase {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -7136030399517539957L;

	public static final String INITIALIZER_NAME = "ParticleSourceRegionInitializer";

	// --------------------------------------
	// Variables
	// --------------------------------------

	public float srcX;
	public float srcY;
	public float srcW;
	public float srcH;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleSourceRegionInitializer() {
		super(INITIALIZER_NAME);
	}

	// --------------------------------------
	// Inherited-Methods
	// --------------------------------------

	@Override
	public void initialize(Particle particle) {
		particle.setupSourceTexture(srcX, srcY, srcW, srcH);
	}
}
