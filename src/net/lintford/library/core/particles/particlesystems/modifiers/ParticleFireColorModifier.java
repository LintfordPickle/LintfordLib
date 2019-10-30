package net.lintford.library.core.particles.particlesystems.modifiers;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.maths.RandomNumbers;
import net.lintford.library.core.particles.Particle;

public class ParticleFireColorModifier extends ParticleModifierBase {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -2141468031095805751L;

	public static final String MODIFIER_NAME = "ParticleFireColorModifier";

	// --------------------------------------
	// Variables
	// --------------------------------------

	public final float mRMin, mRMax;
	public final float mGMin, mBMax;
	public final float mBMin, mGMax;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleFireColorModifier() {
		super(MODIFIER_NAME);

		mRMin = 209f / 255f;
		mRMax = 229f / 255f;

		mGMin = 6f / 255f;
		mGMax = 26f / 255f;

		mBMin = 3f / 255f;
		mBMax = 23f / 255f;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialize(Particle pParticle) {

	}

	@Override
	public void update(LintfordCore pCore) {

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void updateParticle(LintfordCore pCore, Particle pParticle) {
		float normalizedLifetime = pParticle.timeSinceStart / pParticle.lifeTime();

		// we want particles to fade in and fade out, so we'll calculate alpha
		// to be (normalizedLifetime) * (1-normalizedLifetime). this way, when
		// normalizedLifetime is 0 or 1, alpha is 0. the maximum value is at
		// normalizedLifetime = .5, and is
		// (normalizedLifetime) * (1-normalizedLifetime)
		// (.5) * (1-.5)
		// .25
		// since we want the maximum alpha to be 1, not .25, we'll scale the
		// entire equation by 4.
		pParticle.r = getRandomValue(mRMin, mRMax) * normalizedLifetime * (normalizedLifetime - pParticle.r);
		pParticle.g = getRandomValue(mGMin, mGMax) * normalizedLifetime * (normalizedLifetime - pParticle.g);
		pParticle.b = getRandomValue(mBMin, mBMax) * normalizedLifetime * (normalizedLifetime - pParticle.b);

	}

	protected float getRandomValue(final float pMin, final float pMax) {
		if (pMin == pMax) {
			return pMax;

		} else {
			return RandomNumbers.random(pMin, pMax);

		}

	}

}
