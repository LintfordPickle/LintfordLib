package net.ld.library.core.particles.modifiers;

import net.ld.library.core.particles.IParticleModifier;
import net.ld.library.core.particles.Particle;
import net.ld.library.core.time.GameTime;

public class GravityModifier implements IParticleModifier {

	// =============================================
	// Constants
	// =============================================

	private final static float GRAVITY_CONSTANT = 9.8f;

	// =============================================
	// Core-Methods
	// =============================================

	@Override
	public void initialise(Particle pParticle) {

	}

	@Override
	public void update(Particle pParticle, GameTime pGameTime) {
		// FIXME: time missing here
		pParticle.acceleration.y = GRAVITY_CONSTANT *  0.3f;

	}

}
