package net.lintford.library.core.graphics.particles.modifiers;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.particles.Particle;

public class ParticleGravityModifier implements IParticleModifier {

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected float mGravity;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleGravityModifier(final float pGravityValue) {
		mGravity = pGravityValue;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialise(Particle pParticle) {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(LintfordCore pCore) {

	}

	@Override
	public void updateParticle(LintfordCore pCore, Particle pParticle) {
		final float lDeltaTime = (float) pCore.time().elapseGameTimeMilli() / 1000f;
		pParticle.dy += mGravity * lDeltaTime;

	}

}
