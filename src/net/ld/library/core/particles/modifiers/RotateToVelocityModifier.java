package net.ld.library.core.particles.modifiers;

import net.ld.library.core.particles.IParticleModifier;
import net.ld.library.core.particles.Particle;
import net.ld.library.core.time.GameTime;

public class RotateToVelocityModifier implements IParticleModifier {

	// =============================================
	// Core-Methods
	// =============================================

	@Override
	public void initialise(Particle pParticle) {

	}

	@Override
	public void update(Particle pParticle, GameTime pGameTime) {
		
		pParticle.angle = (float)Math.toDegrees(Math.atan2(pParticle.velocity.x, pParticle.velocity.y));
		
	}
}
