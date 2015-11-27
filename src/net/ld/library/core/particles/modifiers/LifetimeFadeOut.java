package net.ld.library.core.particles.modifiers;

import net.ld.library.core.particles.IParticleModifier;
import net.ld.library.core.particles.Particle;
import net.ld.library.core.time.GameTime;

public class LifetimeFadeOut implements IParticleModifier {

	private float mFromTime;

	// =============================================
	// Constructor
	// =============================================

	public LifetimeFadeOut(float pFromTime) {
		mFromTime = pFromTime;
	}

	// =============================================
	// Core-Methods
	// =============================================

	@Override
	public void initialise(Particle pParticle) {

	}

	@Override
	public void update(Particle pParticle, GameTime pGameTime) {

		// FIXME: Introduce that variable above into this code below
		float lPercentage = 0;
		if (pParticle.mTotalLife - pParticle.mLife > mFromTime) {
			lPercentage = (pParticle.mTotalLife - mFromTime - pParticle.mLife) / (pParticle.mTotalLife - mFromTime);
		}

		pParticle.color.w = lerp(1, 0, lPercentage);

	}

	static float lerp(float start, float end, float percent) {
		return (start + percent * (end - start));
	}

}
