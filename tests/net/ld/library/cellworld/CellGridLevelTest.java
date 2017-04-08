package net.ld.library.cellworld;

import org.junit.Test;

public class CellGridLevelTest {

	/** Test the creation of the CellGridLevel */
	@Test
	public void levelCreationTest() {
		// Arrange
		CellGridLevel lLevel = new CellGridLevel(25, 25);

		// Act
		System.out.println("total number tiles: " + lLevel.totalTIles());

		// Assert
		assert (lLevel.totalTIles() == 25 * 25) : "total number of level tiles doesn't match the constructor input";

	}

	/** Test the coordinate handling of the CellGridLevel */
	@Test
	public void levelCoordinateTest() {
		// Arrange
		// ===========================
		CellGridLevel lLevel = new CellGridLevel(25, 25);

		// Act
		// ===========================
		final int lCoordX = 2;
		final int lCoordY = 2;

		final int lStartIndex = lLevel.getIndex(0, 0);
		final int lNormalArrayIndex = lLevel.getIndex(lCoordX, lCoordY);
		final int lEndIndex = lLevel.getIndex(24, 24);

		final int lInvalidIndexNeg = lLevel.getIndex(-2, 2);
		final int lInvalidIndexOOB = lLevel.getIndex(23, 28);

		// Assert
		// ===========================
		// Normal
		assert (lNormalArrayIndex == 2 * 25 + 2) : "Level returned incorrect array index from given input coordinates";

		// Fringe cases
		assert (lStartIndex == 0) : "returned start array index does not equal 0";
		assert (lEndIndex == (25 * 25) - 1) : "returned end array index doesn't match the level dimensions";

		// Invalid checks
		assert (lInvalidIndexNeg == CellGridLevel.INVALID_TILE_INDEX) : "Negative OOB check failed (expected CellGridLevel.INVALID_TILE_INDEX";
		assert (lInvalidIndexOOB == CellGridLevel.INVALID_TILE_INDEX) : "Positive OOB check failed (expected CellGridLevel.INVALID_TILE_INDEX";

	}

}
