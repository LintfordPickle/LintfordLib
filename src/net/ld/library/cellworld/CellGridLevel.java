package net.ld.library.cellworld;

public class CellGridLevel {

	public final int cellsWide;
	public final int cellsHigh;
	int[][] mLevelGrid;
	
	public int[][] levelGrid(){
		return mLevelGrid;
	}
	
	public CellGridLevel(final int pCellsWide, final int pCellsHigh){
		cellsWide = pCellsWide;
		cellsHigh = pCellsHigh;
		
		mLevelGrid = new int[pCellsWide][pCellsHigh];
		
	}
		
}
