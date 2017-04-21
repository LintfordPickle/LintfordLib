package net.ld.library.core.graphics.particles.modifiers;

import net.ld.library.cellworld.collisions.IGridCollider;
import net.ld.library.core.graphics.particles.Particle;
import net.ld.library.core.time.GameTime;

/** Particles collide with ground */
public class ParticleGroundColisionModifier implements IParticleModifier {

	// --------------------------------------
	// Variables
	// --------------------------------------

	IGridCollider mGridCollider;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleGroundColisionModifier(IGridCollider pIGridCollider) {
		mGridCollider = pIGridCollider;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialise(Particle pParticle) {

	}

	@Override
	public void update(GameTime pGameTime) {

	}

	@Override
	public void updateParticle(Particle pParticle, GameTime pGameTime) {
		// TODO: unimplemented method
	}
}
