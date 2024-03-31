package net.lintfordlib.core.particles.particlesystems.modifiers.custom;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.particles.Particle;
import net.lintfordlib.core.particles.particlesystems.modifiers.ParticleModifierBase;

public class ParticleGravityModifier extends ParticleModifierBase {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -9190222394993500218L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public float gravityX;
	public float gravityY;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleGravityModifier() {
		this(0.f, .978f);
	}

	public ParticleGravityModifier(float gravityX, float gravityY) {
		super(ParticleGravityModifier.class.getSimpleName());

		this.gravityX = gravityX;
		this.gravityY = gravityY;
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

		particle.vx += gravityX * lDeltaTime;
		particle.vy += gravityY * lDeltaTime;
	}
}
