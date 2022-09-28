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
	public void initialize(Particle particle) {

	}

	@Override
	public void update(LintfordCore core) {

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void updateParticle(LintfordCore core, Particle particle) {
		final var lNormalizedLifetime = particle.timeSinceStart / particle.lifeTime();
		particle.color.r = getRandomValue(mRMin, mRMax) * lNormalizedLifetime * (lNormalizedLifetime - particle.color.r);
		particle.color.g = getRandomValue(mGMin, mGMax) * lNormalizedLifetime * (lNormalizedLifetime - particle.color.g);
		particle.color.b = getRandomValue(mBMin, mBMax) * lNormalizedLifetime * (lNormalizedLifetime - particle.color.b);
	}

	protected float getRandomValue(final float minValue, final float maxValue) {
		if (minValue == maxValue) {
			return maxValue;
		} else {
			return RandomNumbers.random(minValue, maxValue);
		}
	}
}
