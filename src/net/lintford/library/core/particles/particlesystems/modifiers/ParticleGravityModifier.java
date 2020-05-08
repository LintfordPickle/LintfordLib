package net.lintford.library.core.particles.particlesystems.modifiers;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.particles.Particle;

public class ParticleGravityModifier extends ParticleModifierBase {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -9190222394993500218L;

	public static final String MODIFIER_NAME = "ParticleGravityModifier";

	// --------------------------------------
	// Variables
	// --------------------------------------

	public float gravity;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleGravityModifier() {
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

	@Override
	public void updateParticle(LintfordCore pCore, Particle pParticle) {
		final float lDeltaTime = (float) pCore.appTime().elapsedTimeMilli();
		pParticle.dy += gravity * lDeltaTime;

	}

}
