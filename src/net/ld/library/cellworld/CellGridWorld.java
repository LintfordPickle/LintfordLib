package net.ld.library.cellworld;

import java.util.ArrayList;
import java.util.List;

import net.ld.library.core.time.GameTime;

/**
 * Creates a simple world based on a cell grid. Also contains a collection of
 * level tiles and entities.
 */
public class CellGridWorld {

	// -------------------------------------
	// Variables
	// -------------------------------------

	protected CellGridLevel mLevelTiles;

	public final int cellSize;
	public final int cellsWide;
	public final int cellsHigh;

	protected List<CellWorldEntity> mEntities;
	protected List<CellWorldEntity> mEntitiesToUpdate;

	// -------------------------------------
	// Properties
	// -------------------------------------

	/** Returns a list of entities currently registered in the world. */
	public List<CellWorldEntity> entities() {
		return mEntities;
	}

	// -------------------------------------
	// Constructor
	// -------------------------------------

	/**
	 * ctor. Creates a {@link CellGridLevel} of the given dimensions and sets up
	 * the internal structures for storing {@link CellWorldEntity}s.
	 */
	public CellGridWorld(int pCellSize, int pCellsWide, int pCellsHigh) {
		cellSize = pCellSize;
		cellsWide = pCellsWide;
		cellsHigh = pCellsHigh;

		mEntities = new ArrayList<>(64);
		mEntitiesToUpdate = new ArrayList<>(64);

		mLevelTiles = new CellGridLevel(cellsWide, cellsHigh);

	}

	// -------------------------------------
	// Core-Methods
	// -------------------------------------

	/** Called once after instantiation. */
	public void initialise() {

	}

	/**
	 * Called once per frame, updates the entities registered in the
	 * {@link CellGridWorld}
	 */
	public void update(GameTime pGameTime) {
		mEntitiesToUpdate.clear();

		final int lEntityCount = mEntities.size();
		for (int i = 0; i < lEntityCount; i++) {
			// Only update entities which are in use.
			if (!mEntities.get(i).isInUse())
				continue;

			mEntitiesToUpdate.add(mEntities.get(i));

		}

		final int lEntityUpdateCount = mEntitiesToUpdate.size();
		for (int i = 0; i < lEntityUpdateCount; i++) {
			CellWorldEntity lEntity = mEntitiesToUpdate.get(i);

			lEntity.update(pGameTime);

		}

	}

	// -------------------------------------
	// Methods
	// -------------------------------------

	/**
	 * Checks the level tile at the given coordinates, and returns true if the
	 * tile is non-zero.
	 */
	public boolean hasLevelCollisionAt(int pX, int pY) {
		final int ARRAY_INDEX = mLevelTiles.getIndex(pX, pY);
		if (ARRAY_INDEX == CellGridLevel.INVALID_TILE_INDEX)
			return false;

		// Check world
		return mLevelTiles.mLevelGrid[ARRAY_INDEX] != 0;

	}

	/**
	 * Returns true if the given {@link CellWorldEntity} instance overlaps
	 * spatially another {@link CellWorldEntity} on the level. false is returned
	 * otherwise.
	 */
	public boolean overlaps(CellWorldEntity e) {
		final int lEntityCount = mEntities.size();
		for (int i = 0; i < lEntityCount; i++) {
			CellWorldEntity e1 = mEntities.get(i);
			if (e1 == e)
				continue;
			if (!e1.isInUse())
				continue;

			// TODO: Might not work for larger objects (larger than 1 cell)
			// if (e != e1 && Math.abs(e1.cx - e.cx) <= 2 && Math.abs(e1.cy -
			// e.cy) <= 2) {
			float lMaxDist = mEntities.get(i).radius + e.radius;
			float lDistSqr = (e.xx - e1.xx) * (e.xx - e1.xx) + (e.yy - e1.yy) * (e.yy - e1.yy);

			if (lDistSqr <= lMaxDist * lMaxDist) {
				return true;
			}

		}

		return false;
	}

	/**
	 * Adds the given {@link CellWorldEntity} to the {@link CellGridWorld} and
	 * updates its reference to the world. true is returned if the object is
	 * successfully added, false is returned otherwise.
	 */
	public boolean addEntity(CellWorldEntity pWorldEntity) {
		if (!mEntities.contains(pWorldEntity)) {

			pWorldEntity.attachParent(this);
			mEntities.add(pWorldEntity);

			return true;

		}

		// Apparently this entity already exists in the world.
		return false;

	}

	/**
	 * Removes the given {@link CellWorldEntity} from the world, if it has
	 * previously been registered.
	 */
	public void removeEntity(CellWorldEntity pWorldEntity) {
		if (mEntities.contains(pWorldEntity)) {
			pWorldEntity.detachParent();
			mEntities.remove(pWorldEntity);

		}

	}

}
