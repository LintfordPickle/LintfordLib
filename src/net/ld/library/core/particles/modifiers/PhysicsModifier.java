package net.ld.library.core.particles.modifiers;

import net.ld.library.core.particles.IParticleModifier;
import net.ld.library.core.particles.Particle;
import net.ld.library.core.time.GameTime;

public class PhysicsModifier implements IParticleModifier {

	// =============================================
	// Core-Methods
	// =============================================

	@Override
	public void initialise(Particle pParticle) {

	}

	@Override
	public void update(Particle pParticle, GameTime pGameTime) {
		pParticle.velocity.x += pParticle.acceleration.x * pGameTime.elapseGameTime() / 1000.0f;
		pParticle.velocity.y += pParticle.acceleration.y* pGameTime.elapseGameTime() / 1000.0f;

		pParticle.position.x += pParticle.velocity.x;
		pParticle.position.y += pParticle.velocity.y;

		pParticle.acceleration.x = 0f;
		pParticle.acceleration.y = 0f;
	}
}
