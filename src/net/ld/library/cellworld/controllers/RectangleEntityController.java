package net.ld.library.cellworld.controllers;

import net.ld.library.cellworld.CellEntity;
import net.ld.library.cellworld.EntityManager;
import net.ld.library.cellworld.RectangleEntity;
import net.ld.library.cellworld.collisions.IEntityCollider;
import net.ld.library.cellworld.collisions.IGridCollider;
import net.ld.library.core.maths.MathHelper;
import net.ld.library.core.time.GameTime;

/** Controller a single instance of {@link CellEntity}. */
public class RectangleEntityController {

	// -------------------------------------
	// Constants
	// -------------------------------------

	public static final float EPSILON = 0.001f;
	public static final float MOVEMENT_EPSILON = 0.3f;

	// -------------------------------------
	// Variables
	// -------------------------------------

	/** If a {@link IGridCollider} object is available, each of the {@link RectangleEntity}s in the {@code mEntityManager} will be checked for collisions against it. */
	protected IGridCollider mGridCollider;

	/** If a {@link IEntityCollider} object is available, each of the {@link RectangleEntity}s in the {@code mEntityManager} will be checked for collisions against it. */
	protected IEntityCollider mEntityColliders;

	protected EntityManager mEntityManager;

	public float gravity;
	public float frictionX;
	public float frictionY;

	// -------------------------------------
	// Properties
	// -------------------------------------

	/** The {@link RectangleEntityController} is considered initialized if it holds a valid reference to an {@link EntityManager}. */
	public boolean isInitialised() {
		return mEntityManager != null;
	}

	public void setGridCollider(IGridCollider pGridCollider) {
		mGridCollider = pGridCollider;
	}

	public void setEntityCollider(IEntityCollider pEntityCollider) {
		mEntityColliders = pEntityCollider;
	}

	// -------------------------------------
	// Constructor
	// -------------------------------------

	/** constructor, nothing to see. */
	public RectangleEntityController() {

	}

	// -------------------------------------
	// Core-Methods
	// -------------------------------------

	/** Initializes the {@link RectangleEntityController} with an {@link EntityManager}. All {@link RectangleEntity}s within the {@link EntityManager} will be updated. */
	public void initialise(EntityManager pEntityManager) {
		mEntityManager = pEntityManager;

	}

	public void update(GameTime pGameTime) {
		if (!isInitialised())
			return;

		// Iterate through the RectangleEntities in the EntityManager and update them
		final int MOB_COUNT = mEntityManager.entities().size();
		for (int i = 0; i < MOB_COUNT; i++) {
			final CellEntity CELL_ENTITY = mEntityManager.entities().get(i);

			if (CELL_ENTITY == null)
				continue;

			if (CELL_ENTITY instanceof RectangleEntity) {
				updateCharacterPhysics(pGameTime, (RectangleEntity) CELL_ENTITY);

				// TODO (John): Entity STATES can be derived here from the current properties of the object

			}

		}

	}

	// TODO (John): Remove some code pertaining to leftFacing and isOnGround (this should be added somewhere else).
	// TODO (John): This code doesn't work if the RectangleEntities are smaller than CELL_SIZE
	// TODO (John): Remove the hardcoded CELL_SIZE (this is dependent on the CellGridLevel).
	protected void updateCharacterPhysics(GameTime pGameTime, RectangleEntity pCharacter) {
		if (pCharacter == null)
			return;

		// TODO (John): Get the actual world grid size from somewhere.
		final int CELL_SIZE = 64;
		float lDelta = (float) (pGameTime.elapseGameTime() / 1000.0f);

		int blockSize_x = 1 + (int) (pCharacter.width / 2 / CELL_SIZE);
		int blockSize_y = 1 + (int) (pCharacter.height / 2 / CELL_SIZE);

		// REPEL ENTITY CODE
		checkEntityCollisions(pGameTime, pCharacter);

		// X component
		pCharacter.rx += pCharacter.dx * lDelta;

		// TODO: Apply different friction depending on in air etc.
		pCharacter.dx *= frictionX;

		// Figure out, based on the width of the character, how much 'room' is left in the edge cells
		final float SIZE_REMAINING_X = ((pCharacter.width / 2) % CELL_SIZE) / CELL_SIZE;
		final float SIZE_REMAINING_Y = ((pCharacter.height / 2) % CELL_SIZE) / CELL_SIZE;

		// EPISON
		if (Math.abs(pCharacter.dx) < MOVEMENT_EPSILON) {
			pCharacter.dx = 0;

		} else {
			// Check collisions on the X-Axis
			for (int y = -blockSize_y; y < blockSize_y; y++) {

				// Because we potentially have world entities which are larger than a single cell, we need to check for level collisions which several blocks on each axis.
				// Furthermore, because entity sizes are not always multiples of CELL_SIZE, we need to check if the entity is even present in some of the neighboring cells before
				// proceeding with the collision checks.

				final float TILE_CENTER_Y = ((pCharacter.cy + y) * CELL_SIZE + CELL_SIZE / 2) + (MathHelper.clamp(-y, -1, 1) * CELL_SIZE / 2);
				final float CHARACTER_CENTER_Y = (pCharacter.yy);
				if (Math.abs(TILE_CENTER_Y - CHARACTER_CENTER_Y) >= pCharacter.height / 2)
					continue;

				if (pCharacter.dx < 0 && hasLevelCollision(pCharacter.cx - blockSize_x, pCharacter.cy + y) && pCharacter.rx < SIZE_REMAINING_X) {
					pCharacter.dx = 0;
					pCharacter.rx = SIZE_REMAINING_X;
				}
				if (pCharacter.dx > 0 && hasLevelCollision(pCharacter.cx + blockSize_x, pCharacter.cy + y) && pCharacter.rx > 1 - SIZE_REMAINING_X) {
					pCharacter.dx = 0;
					pCharacter.rx = 1 - SIZE_REMAINING_X;
				}

			}

			while (pCharacter.rx < 0) {
				pCharacter.cx--;
				pCharacter.rx++;
			}
			while (pCharacter.rx > 1) {
				pCharacter.cx++;
				pCharacter.rx--;
			}

		}

		if (pCharacter.dx != 0) {
			if (pCharacter.dx < 0)
				pCharacter.isLeftFacing = true;

			else
				pCharacter.isLeftFacing = false;

		}

		// Y component
		pCharacter.dy += gravity * 2 * lDelta;
		pCharacter.ry += pCharacter.dy * lDelta;

		// Check collisions on the Y-Axis
		boolean onFloorThisTurn = false;
		for (int x = -blockSize_x; x <= blockSize_x; x++) {

			// Because we potentially have world entities which are larger than a single cell, we need to check for level collisions which several blocks on each axis.
			// Furthermore, because entity sizes are not always multiples of CELL_SIZE, we need to check if the entity is even present in some of the neighboring cells before
			// proceeding with the collision checks.

			final float TILE_CENTER_X = ((pCharacter.cx + x) * CELL_SIZE + CELL_SIZE / 2) + (MathHelper.clamp(-x, -1, 1) * CELL_SIZE / 2);
			final float CHARACTER_CENTER_X = (pCharacter.xx);
			if (Math.abs(TILE_CENTER_X - CHARACTER_CENTER_X) >= pCharacter.width / 2)
				continue;

			// Collision with ceiling
			if (pCharacter.dy < 0 && hasLevelCollision(pCharacter.cx + x, pCharacter.cy - blockSize_y) && pCharacter.ry < SIZE_REMAINING_Y) {
				pCharacter.dy = 0;
				pCharacter.ry = SIZE_REMAINING_Y;
			}

			// Collision with ground
			if (pCharacter.dy > 0 && hasLevelCollision(pCharacter.cx + x, pCharacter.cy + blockSize_y) && pCharacter.ry > 1 - SIZE_REMAINING_Y) {
				pCharacter.dy = 0;
				pCharacter.ry = 1 - SIZE_REMAINING_Y;
				pCharacter.isOnGround = true;
				onFloorThisTurn = true;

			} else {
				if (!onFloorThisTurn) {
					pCharacter.isOnGround = false;

				}

			}

		}

		pCharacter.dy *= frictionY;

		// EPISON
		if (Math.abs(pCharacter.dy) < MOVEMENT_EPSILON) {
			pCharacter.dy = 0;

		}

		while (pCharacter.ry < 0) {
			pCharacter.cy--;
			pCharacter.ry++;
		}
		while (pCharacter.ry > 1) {
			pCharacter.cy++;
			pCharacter.ry--;
		}

		// Update the final position of the particle (used for rendering sprites etc.)
		pCharacter.xx = (pCharacter.cx + pCharacter.rx) * CELL_SIZE;
		pCharacter.yy = (pCharacter.cy + pCharacter.ry) * CELL_SIZE;

	}

	/** If a valid {@link IEntityCollider} reference is available, we will use it to check for collision against the given CellEntity. */
	protected void checkEntityCollisions(GameTime pGameTime, CellEntity pCellWorldEntity) {
		// Check this entity against other entities in the world

	}

	/** If a valid {@link IGridCollider} reference is available, we will use it to check for collision against the given point. */
	protected boolean hasLevelCollision(int pCX, int pCY) {
		// To prevent the character from dropping through the level when the level is loading, we assume there are collisions until
		// we can check otherwise

		if (mGridCollider == null)
			return true;

		return mGridCollider.hasGridCollision(pCX, pCY);

	}

}
