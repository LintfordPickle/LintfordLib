package net.lintfordlib.core.particles.particlesystems.modifiers.custom;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.maths.RandomNumbers;
import net.lintfordlib.core.particles.Particle;
import net.lintfordlib.core.particles.particlesystems.modifiers.ParticleModifierBase;

public class ParticleRandomRotationModifierWithDrag extends ParticleModifierBase {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -3528664477677918803L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public float minRandAmount = 0.f;
	public float maxRandAmount = 10.f;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleRandomRotationModifierWithDrag() {
		super(ParticleRandomRotationModifierWithDrag.class.getSimpleName());

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

		particle.angVel += RandomNumbers.random(minRandAmount, maxRandAmount);
		particle.rotationInRadians += particle.angVel * lDelta;
		particle.angVel *= 0.98f;
	}
}
