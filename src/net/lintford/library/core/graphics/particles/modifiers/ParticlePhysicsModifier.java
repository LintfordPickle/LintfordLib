package net.lintford.library.core.graphics.particles.modifiers;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.particles.Particle;

public class ParticlePhysicsModifier implements IParticleModifier {

	// --------------------------------------
	// Constants
	// --------------------------------------

	/** The factor of the particle vertical velocity to conserve after collisions with the floor */
	public static final float PARTICLE_FLOOR_BOUNCE_AMT = 0.5f;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticlePhysicsModifier() {

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialise(Particle pParticle) {

	}

	@Override
	public void update(LintfordCore pCore) {

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void updateParticle(LintfordCore pCore, Particle pParticle) {
		float lDelta = (float) pCore.time().elapseGameTimeMilli() / 1000f;

		// X component
		pParticle.x += pParticle.dx * lDelta;
		pParticle.y += pParticle.dy * lDelta;

		pParticle.dy *= 0.98f; // ConstantsTable.FRICTION_Y;

	}

}
