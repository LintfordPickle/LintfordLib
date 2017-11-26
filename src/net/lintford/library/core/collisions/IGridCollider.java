package net.lintford.library.core.collisions;

public interface IGridCollider {

	/** Returns true if the cell at cell position ({@link pCellX},{@link pCellY}) is solid and false otherwise. */
	public abstract boolean hasGridCollision(int pWorldTileX, int pWorldTileY);

	/** Returns true if the cell at world position ({@link pWorldX},{@link pWorldY}) is solid and false otherwise. */
	public default boolean hasGridCollision(int pWorldPositionX, int pWorldPositiY, int pCellSize) {
		if (pCellSize <= 0)
			return false;

		return hasGridCollision(pWorldPositionX / pCellSize, pWorldPositiY / pCellSize);

	}

}
