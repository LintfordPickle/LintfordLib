package net.lintfordlib.core.particles.particlesystems.modifiers;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.particles.Particle;

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
	public void initialize(Particle particle) {

	}

	@Override
	public void update(LintfordCore core) {

	}

	@Override
	public void updateParticle(LintfordCore core, Particle particle) {
		final var lDeltaTime = (float) core.appTime().elapsedTimeMilli();
		particle.dy += gravity * lDeltaTime;
	}
}
