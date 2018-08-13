package net.lintford.library.core.graphics.particles.modifiers;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.particles.Particle;

public class ParticleScaleModifier implements IParticleModifier {

	// --------------------------------------
	// Constants
	// --------------------------------------

	/** The factor of the particle vertical velocity to conserve after collisions with the floor */
	public static final float PARTICLE_FLOOR_BOUNCE_AMT = 0.5f;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleScaleModifier(float pStartValue, float pEndValue) {

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialise(Particle pParticle) {

	}

	@Override
	public void update(LintfordCore pCore) {

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void updateParticle(LintfordCore pCore, Particle pParticle) {
		float normalizedLifetime = pParticle.timeSinceStart / pParticle.lifeTime();

		pParticle.scale = (1 - normalizedLifetime);

	}

}
