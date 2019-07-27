package net.lintford.library.core.graphics.particles.initialisers;

import net.lintford.library.core.graphics.particles.Particle;

/**
 * Sets the initial source region of a {@link Particle} when it is spawned in the {@link SimpleAnimationRenderer}. This determines which area of the texture the particle takes the
 * image from.
 */
public class ParticleSourceRegionInitializer implements IParticleInitializer {

	float mSrcX;
	float mSrcY;
	float mSrcW;
	float mSrcH;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleSourceRegionInitializer(float pSX, float pSY, float pSW, float pSH) {
		mSrcX = pSX;
		mSrcY = pSY;
		mSrcW = pSW;
		mSrcH = pSH;

	}

	// --------------------------------------
	// Inherited-Methods
	// --------------------------------------

	@Override
	public void initialize(Particle pParticle) {
		pParticle.setupSourceTexture(mSrcX, mSrcY, mSrcW, mSrcH);

	}

}
