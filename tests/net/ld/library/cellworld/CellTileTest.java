package net.ld.library.cellworld;

import org.junit.Test;

public class CellTileTest {

	/** Tests the state of a {@link CellTile} after creation (it should be ready to use). */
	@Test
	public void cellTileCreationTest() {

		// Arrange
		CellTile lDirtTile = new CellTile(1, "Dirt");
		
		// Act
		
		// Assert
		assert(lDirtTile.tileIndex == 1) : "Internal cell tile index is not consistent with actual parameter passed to constructor ";
		assert(lDirtTile.tileName.equals("Dirt")) : "Internal cell tile name is not consistent with actual parameter passed to constructor.";
		assert(lDirtTile.movementModifier() == 1f) : "Default movement modifer of CellTile is not set to 1f";
		
	}
	
	/** Tests the state of a {@link CellTile} after creation (it should be ready to use). */
	@Test
	public void cellTileMovementTest() {

		// Arrange
		CellTile lDirtTile = new CellTile(1, "Dirt");
		CellTile lWallTile = new CellTile(2, "Wall");
		CellTile lInvalidMovementTile = new CellTile(3, "Invalid")
	;	
		// Act
		lDirtTile.movementModifier(0.9f);
		lWallTile.movementModifier(0f);
		lInvalidMovementTile.movementModifier(-0.1f);
		
		// Assert
		assert(lDirtTile.movementModifier() == 0.9f) : "CellTile movement modifier is not correct when set to below zero value";
		assert(lWallTile.movementModifier() == 0f) : "CellTile movement modifier is not correct when set zero value";
		assert(lInvalidMovementTile.movementModifier() == 0) : "CellTile movement modifier is not correct when set to below zero value";
		
	}

}
