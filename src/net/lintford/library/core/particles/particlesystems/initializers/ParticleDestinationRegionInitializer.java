package net.lintford.library.core.particles.particlesystems.initializers;

import net.lintford.library.core.particles.Particle;

/** Sets the initial size of a {@link Particle} when it is spawned in the {@link SimpleAnimationRenderer}. This is effectively setting the particle dimensions. */
public class ParticleDestinationRegionInitializer extends ParticleInitializerBase {

	// --------------------------------------
	// Constructor
	// --------------------------------------

	private static final long serialVersionUID = -5808905634238452898L;

	public static final String INITIALIZER_NAME = "ParticleDestinationRegionInitializer";

	// --------------------------------------
	// Variables
	// --------------------------------------

	public float width;
	public float height;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleDestinationRegionInitializer() {
		super(INITIALIZER_NAME);

	}

	// --------------------------------------
	// Inherited-Methods
	// --------------------------------------

	@Override
	public void initialize(Particle pParticle) {
		pParticle.setupDestTexture(width, height);

	}

}
