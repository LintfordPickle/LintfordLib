package net.lintford.library.core.graphics.particles.modifiers;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.collisions.IGridCollider;
import net.lintford.library.core.graphics.particles.Particle;

/** Particles collide with ground */
public class ParticleGroundColisionModifier implements IParticleModifier {

	// --------------------------------------
	// Variables
	// --------------------------------------

	IGridCollider mIGridCollider;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleGroundColisionModifier(IGridCollider pIGridCollider) {
		mIGridCollider = pIGridCollider;
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

	@Override
	public void updateParticle(Particle pParticle, LintfordCore pCore) {
		// TODO: unimplemented method
	}
}
