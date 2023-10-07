package net.lintfordlib.core.particles.particlesystems.modifiers;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.maths.RandomNumbers;
import net.lintfordlib.core.particles.Particle;

public class ParticleWispMovementModifier extends ParticleModifierBase {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 6797729588507702695L;

	public static final String MODIFIER_NAME = "ParticleWispMovementModifier";

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleWispMovementModifier() {
		super(MODIFIER_NAME);

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void initialize(Particle particle) {
		// Give each Wisp particle a random direction
		final var lInitialAngle = RandomNumbers.RANDOM.nextFloat() * (float) Math.PI * 2f;

		particle.dx += (float) Math.cos(lInitialAngle);
		particle.dy += (float) Math.sin(lInitialAngle);
	}

	@Override
	public void update(LintfordCore core) {

	}

	@Override
	public void updateParticle(LintfordCore core, Particle particle) {
		float lAngle = (float) Math.atan2(particle.dy, particle.dx);
		lAngle += RandomNumbers.RANDOM.nextFloat() * 6f;

		particle.dx += (float) Math.cos(lAngle);
		particle.dy += (float) Math.sin(lAngle);
	}
}
