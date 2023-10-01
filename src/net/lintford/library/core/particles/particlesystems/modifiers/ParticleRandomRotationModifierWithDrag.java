package net.lintford.library.core.particles.particlesystems.modifiers;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.maths.RandomNumbers;
import net.lintford.library.core.particles.Particle;

public class ParticleRandomRotationModifierWithDrag extends ParticleModifierBase {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -3528664477677918803L;

	public static final String MODIFIER_NAME = "ParticleRandomRotationModifierWithDrag";

	// --------------------------------------
	// Variables
	// --------------------------------------

	public float minRandAmount = 0.f;
	public float maxRandAmount = 10.f;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleRandomRotationModifierWithDrag() {
		super(MODIFIER_NAME);

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialize(Particle particle) {
		if (minRandAmount > maxRandAmount)
			minRandAmount = 0.f;
	}

	@Override
	public void update(LintfordCore core) {

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void updateParticle(LintfordCore core, Particle particle) {
		final var lDelta = (float) core.appTime().elapsedTimeMilli();

		particle.dr += RandomNumbers.random(minRandAmount, maxRandAmount);
		particle.rotationInRadians += particle.dr * lDelta;
		particle.dr *= 0.98f;
	}
}
