package net.lintford.library.data.cellworld;

import net.lintford.library.core.collisions.IGridCollider;

public class CellGridLevel implements IGridCollider {

	// -------------------------------------
	// Constants
	// -------------------------------------

	/** This index represents an out of bounds index in the level grid array. */
	public final static int INVALID_TILE_INDEX = -1;

	// -------------------------------------
	// Variables
	// -------------------------------------

	public final int cellSize;
	public final int cellsWide;
	public final int cellsHigh;

	public final int totalTiles;

	protected int[] mLevelGrid;

	// -------------------------------------
	// Properties
	// -------------------------------------

	/** Returns the total number of tiles in this level */
	public int totalTIles() {
		return cellsWide * cellsHigh;
	}

	/** Returns the integer array of level cells. Each cell contains an index of a particular tile type. */
	public int[] levelGrid() {
		return mLevelGrid;
	}

	/** Returns the level tile array index of the given x,y Cartesian coordinate. INVALID_TILE_INDEX is returned if the coordinates given are out of bounds. */
	public int getIndex(int pX, int pY) {
		if (pX < 0 || pX >= cellsWide)
			return -1;
		if (pY < 0 || pY >= cellsHigh)
			return -1;

		return pY * cellsWide + pX;
	}

	// -------------------------------------
	// Constructor
	// -------------------------------------

	/** Constructor for the a cell-based (grid) level. */
	public CellGridLevel(final int pCellSize, final int pCellsWide, final int pCellsHigh) {
		cellSize = pCellSize;
		cellsWide = pCellsWide;
		cellsHigh = pCellsHigh;

		totalTiles = cellsWide * cellsHigh;

		mLevelGrid = new int[pCellsWide * pCellsHigh];

	}

	// -------------------------------------
	// Inherited Methods
	// -------------------------------------

	public int[] getGrid() {
		return mLevelGrid;
	}

	public int getGridHeight() {
		return cellsHigh;
	}

	public int getGridWidth() {
		return cellsWide;
	}

	@Override
	public boolean hasGridCollision(int pWorldTileX, int pWorldTileY) {
		// TODO Auto-generated method stub
		return false;
	}

}
