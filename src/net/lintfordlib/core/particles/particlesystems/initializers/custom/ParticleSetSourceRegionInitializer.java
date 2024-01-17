package net.lintfordlib.core.particles.particlesystems.initializers.custom;

import net.lintfordlib.core.particles.Particle;
import net.lintfordlib.core.particles.particlesystems.initializers.ParticleInitializerBase;

/**
 * Sets the initial source region of a {@link Particle} when it is spawned in the {@link SimpleAnimationRenderer}. This determines which area of the texture the particle takes the image from.
 */
public class ParticleSetSourceRegionInitializer extends ParticleInitializerBase {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -7136030399517539957L;

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

	public ParticleSetSourceRegionInitializer() {
		this(0.f, 0.f, 10.f, 10.f);
	}

	public ParticleSetSourceRegionInitializer(float srcX, float srcY, float srcW, float srcH) {
		super(ParticleSetSourceRegionInitializer.class.getSimpleName());

		this.srcX = srcX;
		this.srcY = srcY;
		this.srcW = srcW;
		this.srcH = srcH;
	}

	// --------------------------------------
	// Inherited-Methods
	// --------------------------------------

	@Override
	public void initialize(Particle particle) {
		particle.setupSourceTexture(srcX, srcY, srcW, srcH);
	}
}
