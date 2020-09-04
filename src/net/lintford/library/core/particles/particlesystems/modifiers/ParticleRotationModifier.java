package net.lintford.library.core.particles.particlesystems.modifiers;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.maths.RandomNumbers;
import net.lintford.library.core.particles.Particle;

public class ParticleRotationModifier extends ParticleModifierBase {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -3528664477677918803L;

	public static final String MODIFIER_NAME = "ParticleRotationModifier";

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleRotationModifier() {
		super(MODIFIER_NAME);

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
		float lDelta = (float) pCore.appTime().elapsedTimeMilli();

		pParticle.dr += RandomNumbers.random(0, 0.00f);

		// X component
		pParticle.rotationInRadians += pParticle.dr * lDelta;

		pParticle.dr *= 0.98f; // ConstantsTable.FRICTION_Y;

	}

}
