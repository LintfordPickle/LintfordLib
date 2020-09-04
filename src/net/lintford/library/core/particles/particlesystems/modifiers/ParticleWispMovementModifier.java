package net.lintford.library.core.particles.particlesystems.modifiers;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.maths.RandomNumbers;
import net.lintford.library.core.particles.Particle;

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
	public void initialize(Particle pParticle) {
		// Give each Wisp particle a random direction
		float lInitialAngle = RandomNumbers.RANDOM.nextFloat() * (float) Math.PI * 2f;

		pParticle.dx += (float) Math.cos(lInitialAngle);
		pParticle.dy += (float) Math.sin(lInitialAngle);

	}

	@Override
	public void update(LintfordCore pCore) {

	}

	@Override
	public void updateParticle(LintfordCore pCore, Particle pParticle) {
		float lAngle = (float) Math.atan2(pParticle.dy, pParticle.dx);
		lAngle += RandomNumbers.RANDOM.nextFloat() * 10f;

		pParticle.dx += (float) Math.cos(lAngle);
		pParticle.dy += (float) Math.sin(lAngle);

	}

}
