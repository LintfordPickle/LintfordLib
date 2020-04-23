package net.lintford.library.core.particles.particlesystems.modifiers;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.particles.Particle;

public class ParticlePhysicsModifier extends ParticleModifierBase {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 3823714289519272208L;

	public static final String MODIFIER_NAME = "ParticlePhysicsModifier";

	/** The factor of the particle vertical velocity to conserve after collisions with the floor */
	public static final float PARTICLE_FLOOR_BOUNCE_AMT = 0.5f;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticlePhysicsModifier() {
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
		float lDelta = (float) pCore.time().elapseAppTimeMilli() / 1000f;

		pParticle.x += pParticle.dx * lDelta;
		pParticle.y += pParticle.dy * lDelta;
		pParticle.rot += Math.toRadians(pParticle.dr * lDelta);

	}

}
