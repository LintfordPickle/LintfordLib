package net.ld.library.core.graphics.particles.modifiers;

import net.ld.library.core.graphics.particles.Particle;
import net.ld.library.core.time.GameTime;

public class ParticleLifetimeModifier implements IParticleModifier {

	// --------------------------------------
	// Constants
	// --------------------------------------

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
		pParticle.timeSinceStart += pGameTime.elapseGameTimeMilli();
		if (pParticle.timeSinceStart >= pParticle.lifeTime()) {
			// kill the particle
			pParticle.reset();

		}

	}

}
