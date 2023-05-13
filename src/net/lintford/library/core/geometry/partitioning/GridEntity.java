package net.lintford.library.core.geometry.partitioning;

import net.lintford.library.core.entities.Entity;

public abstract class GridEntity extends Entity {

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	public int gridEntityType;

	// -caching
	public int minX;
	public int minY;

	public int maxX;
	public int maxY;

	public int queryId;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public boolean isOnGrid() {
		return minX != -1 && minY != -1 && maxX != -1 && maxY != -1;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public GridEntity(int entityUid, int GridEntityType) {
		super(entityUid);

		this.gridEntityType = GridEntityType;
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public void clearGridCache() {
		queryId = 0;
		minX = 0;
		maxX = 0;
		minY = 0;
		maxY = 0;
	}

	public abstract void fillEntityBounds(SpatialHashGrid<?> grid);

	public abstract boolean isGridCacheOld(SpatialHashGrid<?> grid);

}
