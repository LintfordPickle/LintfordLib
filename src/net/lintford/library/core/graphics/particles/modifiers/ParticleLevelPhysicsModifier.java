package net.lintford.library.core.graphics.particles.modifiers;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.collisions.IEntityCollider;
import net.lintford.library.core.collisions.IGridCollider;
import net.lintford.library.core.collisions.LevelCollisions;
import net.lintford.library.core.collisions.PhysicsState;
import net.lintford.library.core.graphics.particles.Particle;
import net.lintford.library.core.maths.RandomNumbers;

/***/
@Deprecated
public class ParticleLevelPhysicsModifier implements IParticleModifier {

	// --------------------------------------
	// Constants
	// --------------------------------------

	/** The factor of the particle vertical velocity to conserve after collisions with the floor */
	public static final float PARTICLE_FLOOR_BOUNCE_AMT = 0.5f;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private PhysicsState mPhysicsState;
	public IGridCollider mLevelGridCollider;
	public IEntityCollider mEntityCollider;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void setGridCollider(IGridCollider pGridCollider) {
		mLevelGridCollider = pGridCollider;

	}

	public void setEntityCollider(IEntityCollider pEntityCollider) {
		mEntityCollider = pEntityCollider;

	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleLevelPhysicsModifier() {
		mPhysicsState = new PhysicsState();

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialize(Particle pParticle) {

	}

	@Override
	public void update(LintfordCore pCore) {

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void updateParticle(LintfordCore pCore, Particle pParticle) {
		float lDelta = (float) pCore.time().elapseGameTimeMilli();

		checkEntityCollisions(pCore, pParticle);

		// X component
//		pParticle.x += pParticle.dx * lDelta;
//		pParticle.dx *= 0.98f; // ConstantsTable.FRICTION_X;
//		if (pParticle.dx < 0 && LevelCollisions.hasLeftCollision(mLevelGridCollider, pParticle, mPhysicsState)) {
//			pParticle.x = mPhysicsState.collisionX + pParticle.radius;
//			pParticle.dx = -pParticle.dx * 0.5f;
//		}
//		if (pParticle.dx > 0 && LevelCollisions.hasRightCollision(mLevelGridCollider, pParticle, mPhysicsState)) {
//			pParticle.x = mPhysicsState.collisionX - pParticle.radius;
//			pParticle.dx = -pParticle.dx * .5f;
//		}
//
//		// Y component
//		pParticle.y += pParticle.dy * lDelta;
//
//		// Ceiling collision
//		if (pParticle.dy < 0 && LevelCollisions.hasCeiling(mLevelGridCollider, pParticle, mPhysicsState)) {
//			pParticle.y = mPhysicsState.collisionY + pParticle.radius;
//			pParticle.dy = -pParticle.dy;
//
//		}
//
//		// floor collision
//		if (pParticle.dy > 0 && LevelCollisions.hasGround(mLevelGridCollider, pParticle, mPhysicsState)) {
//			pParticle.y = mPhysicsState.collisionY - pParticle.radius;
//			pParticle.dy = -Math.abs(pParticle.dy) * PARTICLE_FLOOR_BOUNCE_AMT + RandomNumbers.RANDOM.nextFloat() * 0.4f;
//
//		}
//
//		pParticle.dy *= 0.98f; // ConstantsTable.FRICTION_Y;

	}

	private void checkEntityCollisions(LintfordCore pCore, Particle pParticle) {
		if (mEntityCollider == null)
			return;

		mEntityCollider.checkEntityCollisions(pParticle);

	}

}
