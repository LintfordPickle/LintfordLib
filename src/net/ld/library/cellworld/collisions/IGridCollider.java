package net.ld.library.cellworld.collisions;

import net.ld.library.cellworld.controllers.RectangleEntityController;

/** The {@link IGridCollider} defines an interface which allows you to create grid-based objects which can be used by the {@link RectangleEntityController} to detect when entities collide with said grid. */
public interface IGridCollider {

	// -------------------------------------
	// Methods
	// -------------------------------------

	/** Returns true if the cell at cell position ({@link pCellX},{@link pCellY}) is solid and false otherwise. */
	public abstract boolean hasGridCollision(final int pWorldTileX, final int pWorldTileY);

	/** Returns true if the cell at world position ({@link pWorldX},{@link pWorldY}) is solid and false otherwise. */
	public default boolean hasGridCollision(final int pWorldPositionX, final int pWorldPositiY, final int pCellSize) {
		if (pCellSize <= 0)
			return false;

		return hasGridCollision(pWorldPositionX / pCellSize, pWorldPositiY / pCellSize);

	}

}
