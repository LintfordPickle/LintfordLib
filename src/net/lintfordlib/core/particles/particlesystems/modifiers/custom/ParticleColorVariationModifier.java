package net.lintfordlib.core.particles.particlesystems.modifiers.custom;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.maths.RandomNumbers;
import net.lintfordlib.core.particles.Particle;
import net.lintfordlib.core.particles.particlesystems.modifiers.ParticleModifierBase;

// slightly modifies the RGB of a particles color by a random amount.
public class ParticleColorVariationModifier extends ParticleModifierBase {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -2141468031095805751L;

	public static final String MODIFIER_NAME = "ParticleColorVariationModifier";

	// --------------------------------------
	// Variables
	// --------------------------------------

	public float minRed, maxRed;
	public float minGreen, maxGreen;
	public float minBlue, maxBlue;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleColorVariationModifier() {
		super(MODIFIER_NAME);

		minRed = 209f / 255f;
		maxRed = 229f / 255f;

		minGreen = 6f / 255f;
		maxGreen = 26f / 255f;

		minBlue = 3f / 255f;
		maxBlue = 23f / 255f;

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

		particle.color.r = getRandomValue(minRed, maxRed) * lNormalizedLifetime * (lNormalizedLifetime - particle.color.r);
		particle.color.g = getRandomValue(minGreen, maxGreen) * lNormalizedLifetime * (lNormalizedLifetime - particle.color.g);
		particle.color.b = getRandomValue(minBlue, maxBlue) * lNormalizedLifetime * (lNormalizedLifetime - particle.color.b);
	}

	protected float getRandomValue(final float minValue, final float maxValue) {
		if (minValue == maxValue) {
			return maxValue;
		} else {
			return RandomNumbers.random(minValue, maxValue);
		}
	}
}
