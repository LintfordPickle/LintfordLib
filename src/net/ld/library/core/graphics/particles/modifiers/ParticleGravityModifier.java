package net.ld.library.core.graphics.particles.modifiers;

import net.ld.library.core.graphics.particles.Particle;
import net.ld.library.core.time.GameTime;

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
		pParticle.dy += mGravity * pGameTime.elapseGameTime() / 1000.0f;

	}

}
