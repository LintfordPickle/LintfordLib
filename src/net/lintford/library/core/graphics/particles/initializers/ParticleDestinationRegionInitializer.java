package net.lintford.library.core.graphics.particles.initializers;

import net.lintford.library.core.graphics.particles.Particle;

/** Sets the initial size of a {@link Particle} when it is spawned in the {@link SimpleAnimationRenderer}. This is effectively setting the particle radius. */
public class ParticleDestinationRegionInitializer implements IParticleInitializer {

	protected float mRadius;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleDestinationRegionInitializer(float pParticleSize) {
		mRadius = pParticleSize;

	}

	// --------------------------------------
	// Inherited-Methods
	// --------------------------------------

	@Override
	public void initialize(Particle pParticle) {
		pParticle.setupDestTexture(mRadius);

	}

}
