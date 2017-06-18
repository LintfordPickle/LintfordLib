package net.ld.library.core.graphics.particles.modifiers;

import net.ld.library.cellworld.collisions.IEntityCollider;
import net.ld.library.cellworld.collisions.IGridCollider;
import net.ld.library.cellworld.entities.CellEntity;
import net.ld.library.core.graphics.particles.Particle;
import net.ld.library.core.maths.RandomNumbers;
import net.ld.library.core.time.GameTime;

public class ParticlePhysicsModifier implements IParticleModifier {

	// --------------------------------------
	// Constants
	// --------------------------------------

	/** The factor of the particle vertical velocity to conserve after collisions with the floor */
	public static final float PARTICLE_FLOOR_BOUNCE_AMT = 0.5f;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public IGridCollider mLevelGridCollider;
	public IEntityCollider<CellEntity> mEntityCollider;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void setGridCollider(IGridCollider pGridCollider) {
		mLevelGridCollider = pGridCollider;

	}

	public void setEntityCollider(IEntityCollider<CellEntity> pEntityCollider) {
		mEntityCollider = pEntityCollider;

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

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void updateParticle(Particle pParticle, GameTime pGameTime) {
		float lDelta = (float) (pGameTime.elapseGameTime() / 1000.0f);

		checkEntityCollisions(pParticle, pGameTime);

		// TODO (John): Hard coded CELL_SIZE!
		final float CELL_SIZE = 64;
		float lSizeRemaining = (pParticle.radius % CELL_SIZE) / CELL_SIZE;

		// X component
		pParticle.rx += pParticle.dx * lDelta;
		pParticle.dx *= 0.98f; // ConstantsTable.FRICTION_X;
		if (pParticle.dx < 0 && hasCollision(pParticle.cx - 1, pParticle.cy) && pParticle.rx <= lSizeRemaining) {
			pParticle.dx = -pParticle.dx * 0.5f;
			pParticle.rx = lSizeRemaining;
		}
		if (pParticle.dx > 0 && hasCollision(pParticle.cx + 1, pParticle.cy) && pParticle.rx >= 1 - lSizeRemaining) {
			pParticle.dx = -pParticle.dx * .5f;
			pParticle.rx = 1 - lSizeRemaining;
		}

		while (pParticle.rx < 0) {
			pParticle.cx--;
			pParticle.rx++;
		}

		while (pParticle.rx > 1) {
			pParticle.cx++;
			pParticle.rx--;
		}

		// Y component
		pParticle.ry += pParticle.dy * lDelta;
		pParticle.dy *= 0.98f; // ConstantsTable.FRICTION_Y;

		// Ceiling collision
		if (pParticle.dy < 0 && hasCollision(pParticle.cx, pParticle.cy - 1) && pParticle.ry <= lSizeRemaining) {
			pParticle.dy = 0;
			pParticle.ry = lSizeRemaining;
		}

		// floor collision
		if (pParticle.dy > 0 && hasCollision(pParticle.cx, pParticle.cy + 1) && pParticle.ry >= 1 - lSizeRemaining) {
			pParticle.dy = -pParticle.dy * PARTICLE_FLOOR_BOUNCE_AMT + RandomNumbers.RANDOM.nextFloat() * 0.4f;
			pParticle.ry = 1 - lSizeRemaining;
		}
		while (pParticle.ry < 0) {
			pParticle.cy--;
			pParticle.ry++;
		}
		while (pParticle.ry > 1) {
			pParticle.cy++;
			pParticle.ry--;
		}

		// Update the final position of the particle (used for rendering sprites etc.)
		pParticle.xx = (pParticle.cx + pParticle.rx) * CELL_SIZE;
		pParticle.yy = (pParticle.cy + pParticle.ry) * CELL_SIZE;

	}

	private boolean hasCollision(int pCellGridX, int pCellGridY) {
		if (mLevelGridCollider == null)
			return false;

		int[] levelGrid = mLevelGridCollider.getGrid();
		int levelGridWidth = mLevelGridCollider.getGridWidth();

		return levelGrid[pCellGridY * levelGridWidth + pCellGridX] > 0;

	}

	private void checkEntityCollisions(Particle pParticle, GameTime pGameTime) {
		if (mEntityCollider == null)
			return;

		mEntityCollider.checkEntityCollisions(pParticle);

	}

}
