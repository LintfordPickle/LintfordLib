package net.lintford.library.core.particles.particlesystems.modifiers;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.particles.Particle;

public class ParticleLifetimeModifier extends ParticleModifierBase {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 2284963179665309681L;

	public static final String MODIFIER_NAME = "ParticleLifetimeAlphaFadeInOutModifier";

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleLifetimeModifier() {
		super(MODIFIER_NAME);

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialize(Particle pParticle) {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(LintfordCore pCore) {

	}

	@Override
	public void updateParticle(LintfordCore pCore, Particle pParticle) {

		final float lDeltaTime = (float) pCore.appTime().elapseAppTimeMilli();

		pParticle.timeSinceStart += lDeltaTime;
		if (pParticle.timeSinceStart >= pParticle.lifeTime()) {
			// kill the particle
			pParticle.reset();

		}

	}

}
