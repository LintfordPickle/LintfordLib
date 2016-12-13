package net.ld.library.cellworld;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.ld.library.core.time.GameTime;

/**
 * Creates a simple world based on a cell grid. Also contains a collection of level tiles and entities.
 */
public class CellGridWorld {

	// =============================================
	// Variables
	// =============================================

	protected CellGridLevel mLevelTiles;

	public final int cellSize;
	public final int cellsWide;
	public final int cellsHigh;

	protected List<CellWorldEntity> mEntities;
	protected List<CellWorldEntity> mEntitiesToUpdate;

	// =============================================
	// Properties
	// =============================================

	public List<CellWorldEntity> entities() {
		return mEntities;
	}

	// =============================================
	// Constructor
	// =============================================

	public CellGridWorld(int pCellSize, int pCellsWide, int pCellsHigh) {
		cellSize = pCellSize;
		cellsWide = pCellsWide;
		cellsHigh = pCellsHigh;

		mEntities = new ArrayList<>(64);
		mEntitiesToUpdate = new ArrayList<>(64);

		mLevelTiles = new CellGridLevel(cellsWide, cellsHigh);

	}

	// =============================================
	// Core-Methods
	// =============================================

	public void initialise() {
		Random lRand = new Random();

		for (int x = 0; x < cellsWide; x++) {
			for (int y = 0; y < cellsHigh; y++) {
				mLevelTiles.mLevelGrid[x][y] = lRand.nextInt(10);
				mLevelTiles.mLevelGrid[x][y] = mLevelTiles.mLevelGrid[x][y] > 7 ? 1 : 0;

			}

		}

	}

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

	// =============================================
	// Methods
	// =============================================

	public boolean hasCollisionAt(int pX, int pY) {
		boolean lResult = false;

		if (pX < 0 || pX >= cellsWide)
			return true;
		if (pY < 0 || pY >= cellsHigh)
			return true;

		// Check world
		lResult = mLevelTiles.mLevelGrid[pX][pY] != 0;

		// Check entities

		return lResult;
	}

	public boolean overlaps(CellWorldEntity e) {
		final int lEntityCount = mEntities.size();
		for (int i = 0; i < lEntityCount; i++) {
			CellWorldEntity e1 = mEntities.get(i);
			if (e1 == e)
				continue;
			if (!e1.isInUse())
				continue;

			// TODO: Might not work for larger objects (larger than 1 cell)
			// if (e != e1 && Math.abs(e1.cx - e.cx) <= 2 && Math.abs(e1.cy - e.cy) <= 2) {
			float lMaxDist = mEntities.get(i).radius + e.radius;
			float lDistSqr = (e.xx - e1.xx) * (e.xx - e1.xx) + (e.yy - e1.yy) * (e.yy - e1.yy);

			if (lDistSqr <= lMaxDist * lMaxDist) {
				return true;
			}

			// }

		}

		return false;
	}

	public void addEntity(CellWorldEntity pWorldEntity) {
		if (!mEntities.contains(pWorldEntity)) {

			pWorldEntity.attachParent(this);

			mEntities.add(pWorldEntity);

		}

	}

	public void removeEntity(CellWorldEntity pWorldEntity) {
		if (mEntities.contains(pWorldEntity)) {
			pWorldEntity.detachParent();
			mEntities.remove(pWorldEntity);

		}

	}

}
