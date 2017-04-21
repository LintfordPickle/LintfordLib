package net.ld.library.core.graphics.particles.initialisers;

import net.ld.library.core.graphics.particles.Particle;

/** Sets the initial size of a {@link Particle} when it is spawned in the {@link SimpleAnimationRenderer}. This is effectively setting the particle radius. */
public class ParticleDestinationRegionInitialiser implements IParticleInitialiser {

	protected float mRadius;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleDestinationRegionInitialiser(float pParticleSize) {
		mRadius = pParticleSize;

	}

	// --------------------------------------
	// Inherited-Methods
	// --------------------------------------

	@Override
	public void initialise(Particle pParticle) {
		pParticle.setupDestTexture(mRadius);

	}

}
