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
		pParticle.roy = (float) (RandomNumbers.RANDOM.nextFloat() * Math.PI * 2f);

	}

	@Override
	public void update(LintfordCore pCore) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateParticle(LintfordCore pCore, Particle pParticle) {

		// We will use the un-used rox, roy variables of a particle to track the time and
		// angle of the particle over time.

		pParticle.roy += RandomNumbers.RANDOM.nextFloat() * 10f;

		pParticle.dx += (float) Math.cos(pParticle.roy) * 1f;
		pParticle.dy += (float) Math.sin(pParticle.roy) * 1f;

	}

}
