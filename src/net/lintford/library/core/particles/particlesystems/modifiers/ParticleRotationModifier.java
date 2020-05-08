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

	/** The factor of the particle vertical velocity to conserve after collisions with the floor */
	public static final float PARTICLE_FLOOR_BOUNCE_AMT = 0.5f;

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
		pParticle.rot += pParticle.dr * lDelta;

		pParticle.dr *= 0.98f; // ConstantsTable.FRICTION_Y;

	}

}
