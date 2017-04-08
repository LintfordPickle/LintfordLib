package net.ld.library.cellworld;

public class CellGridLevel {

	// -------------------------------------
	// Constants
	// -------------------------------------
	
	/** This index represents an out of bounds index in the level grid array. */
	public final static int INVALID_TILE_INDEX = -1;
	
	// -------------------------------------
	// Variables
	// -------------------------------------
	
	public final int cellsWide;
	public final int cellsHigh;
	int[] mLevelGrid;
	
	// -------------------------------------
	// Properties
	// -------------------------------------
	
	/** Returns the total number of tiles in this level */
	public int totalTIles() {
		return cellsWide * cellsHigh;
	}
	
	/** Returns the int array of level cells. Each cell contains an index of a particular tile type. */
	public int[] levelGrid(){
		return mLevelGrid;
	}
	
	/** Returns the level tile array index of the given x,y cartesian coordinate. INVALID_TILE_INDEX is returned if the coordinates given are outof bounds. */
	public int getIndex(int pX, int pY) {
		if(pX < 0 || pX >= cellsWide) return -1;
		if(pY < 0 || pY >= cellsHigh) return -1;
		
		return pY * cellsWide + pX;
	}
	
	// -------------------------------------
	// Constructor
	// -------------------------------------
	
	/** ctor for the a cell-based (grid) level. */
	public CellGridLevel(final int pCellsWide, final int pCellsHigh){
		cellsWide = pCellsWide;
		cellsHigh = pCellsHigh;
		
		mLevelGrid = new int[pCellsWide * pCellsHigh];
		
	}
		
}
