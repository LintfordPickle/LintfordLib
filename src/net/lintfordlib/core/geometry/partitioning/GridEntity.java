package net.lintfordlib.core.geometry.partitioning;

import net.lintfordlib.core.entities.Entity;

public abstract class GridEntity extends Entity {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	private static final long serialVersionUID = -5483114829103500322L;

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

	public GridEntity(int entityUid, int gridEntityType) {
		super(entityUid);

		this.gridEntityType = gridEntityType;

		// default to not on grid
		minX = -1;
		minY = -1;
		maxX = -1;
		maxY = -1;
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public void clearGridCache() {
		queryId = -1;
		minX = -1;
		maxX = -1;
		minY = -1;
		maxY = -1;
	}

	public abstract void fillEntityBounds(SpatialHashGrid<?> grid);

	public abstract boolean isGridCacheOld(SpatialHashGrid<?> grid);

}
