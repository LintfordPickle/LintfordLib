package net.ld.library.cellworld;

import net.ld.library.cellworld.entities.CellEntity;

/** A simple class representing a tile in a tile based game world. */
public class CellTile {

	// -------------------------------------
	// Constants
	// -------------------------------------

	/**
	 * Represents an empty tile in the {@link EntityPool}, with an index of
	 * 0.
	 */
	public static final CellTile EMPTY_TILE = new CellTile(0, "Empty");

	// -------------------------------------
	// Variables
	// -------------------------------------

	/** The index of this tile. The tile index can be used to */
	public final int tileIndex;

	/** The name of this tile. */
	public final String tileName;

	/** Returns the movement modifier coefficient for this tile (default 1f) */
	private float mMovementModifier;

	/** Represents if this tile is considered collidable to characters. */
	private boolean mCollidable;

	// -------------------------------------
	// Properties
	// -------------------------------------

	/**
	 * Sets the collidable flag on this {@link CellTile}. Collidable tiles
	 * cannot be entered by a {@link CellEntity}.
	 */
	public void collidable(boolean pNewValue) {
		mCollidable = pNewValue;
	}

	/**
	 * Returns whether or not this tile is colliable by {@link CellEntity}.
	 * A collidable tile cannot be traversed or entered.
	 */
	public boolean collidable() {
		return mCollidable;
	}

	/**
	 * Sets a new value for the movement modifier associated with this tile. The
	 * movement modifier cannot be less than 0.
	 */
	public void movementModifier(float pNewValue) {
		if (pNewValue < 0)
			pNewValue = 0;
		mMovementModifier = pNewValue;
	}

	/**
	 * Returns the movement modifier coefficient associated with movement on
	 * this tile.
	 */
	public float movementModifier() {
		return mMovementModifier;
	}

	// -------------------------------------
	// Constructor
	// -------------------------------------

	/** Instantiates a new CellTile object with the given tile index. */
	public CellTile(final int pTileIndex, final String pName) {
		tileIndex = pTileIndex;
		tileName = pName;

		mMovementModifier = 1f;

	}

}
