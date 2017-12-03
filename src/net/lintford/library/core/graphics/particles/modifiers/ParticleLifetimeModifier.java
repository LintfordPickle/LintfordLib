package net.lintford.library.core.graphics.particles.modifiers;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.particles.Particle;

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
	public void update(LintfordCore pCore) {
		
	}
	
	@Override
	public void updateParticle(Particle pParticle, LintfordCore pCore) {
		
		final float lDeltaTime = (float) pCore.time().elapseGameTimeMilli();
		
		pParticle.timeSinceStart += lDeltaTime;
		if (pParticle.timeSinceStart >= pParticle.lifeTime()) {
			// kill the particle
			pParticle.reset();

		}

	}

}
