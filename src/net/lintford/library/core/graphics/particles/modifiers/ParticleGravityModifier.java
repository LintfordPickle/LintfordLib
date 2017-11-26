package net.lintford.library.core.graphics.particles.modifiers;

import net.lintford.library.core.graphics.particles.Particle;
import net.lintford.library.core.time.GameTime;

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
	public void update(GameTime pGameTime) {

	}

	@Override
	public void updateParticle(Particle pParticle, GameTime pGameTime) {
		final float lDeltaTime = (float) pGameTime.elapseGameTimeMilli() / 1000f;
		pParticle.dy += mGravity * lDeltaTime;

	}

}
