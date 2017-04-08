package net.ld.library.cellworld;

/** A simple class representing a tile in a tile based game world. */
public class CellTile {

	// -------------------------------------
	// Variables
	// -------------------------------------

	/** The index of this tile. The tile index can be used to */
	public final int tileIndex;

	/** The name of this tile. */
	public String tileName;

	/** Returns the movement modifier coefficient for this tile (default 1f) */
	public float movementModifier;
	
	/** Represents if this tile is considered collidable to characters. */
	public boolean collidable;

	// -------------------------------------
	// Constructor
	// -------------------------------------

	/** Instantiates a new CellTile object with the given tile index. */
	public CellTile(final int pTileIndex) {
		tileIndex = pTileIndex;
		movementModifier = 1f;

	}

}
