package net.ld.library.core.particles.modifiers;

import net.ld.library.core.particles.IParticleModifier;
import net.ld.library.core.particles.Particle;
import net.ld.library.core.time.GameTime;

public class DragModifier implements IParticleModifier {

	// =============================================
	// Constants
	// =============================================

	private final static float DRAG_CONSTANT = 0.95f;

	// =============================================
	// Core-Methods
	// =============================================

	@Override
	public void initialise(Particle pParticle) {

	}

	@Override
	public void update(Particle pParticle, GameTime pGameTime) {

		pParticle.velocity.x *= DRAG_CONSTANT;
		pParticle.velocity.y *= DRAG_CONSTANT;

	}
}
