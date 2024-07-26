package net.lintfordlib.core.geometry.partitioning;

import net.lintfordlib.core.entities.Entity;
import net.lintfordlib.core.geometry.Rectangle;

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

	protected GridEntity(int entityUid, int gridEntityType) {
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

	protected void fillEntityBoundsCircle(SpatialHashGrid<?> grid, float wcx, float wcy, float radius) {
		minX = grid.getCellIndexX((int) (wcx - radius));
		minY = grid.getCellIndexY((int) (wcy - radius));

		maxX = grid.getCellIndexX((int) (wcx + radius));
		maxY = grid.getCellIndexY((int) (wcy + radius));
	}

	protected void fillEntityBoundsRectangle(SpatialHashGrid<?> grid, float wcx, float wcy, float width, float height) {
		minX = grid.getCellIndexX((int) (wcx - width / 2));
		minY = grid.getCellIndexY((int) (wcy - height / 2));

		maxX = grid.getCellIndexX((int) (wcx + width / 2));
		maxY = grid.getCellIndexY((int) (wcy + height / 2));
	}

	protected void fillEntityBoundsRectangle(SpatialHashGrid<?> grid, Rectangle aabb) {
		minX = grid.getCellIndexX((int) aabb.left());
		minY = grid.getCellIndexY((int) aabb.top());

		maxX = grid.getCellIndexX((int) aabb.right());
		maxY = grid.getCellIndexY((int) aabb.bottom());
	}

	protected boolean isGridCacheOldCircle(SpatialHashGrid<?> grid, float wcx, float wcy, float radius) {
		final var newMinX = grid.getCellIndexX((int) (wcx - radius));
		final var newMinY = grid.getCellIndexY((int) (wcy - radius));

		final var newMaxX = grid.getCellIndexX((int) (wcx + radius));
		final var newMaxY = grid.getCellIndexY((int) (wcy + radius));

		if (newMinX == minX && newMinY == minY && newMaxX == maxX && newMaxY == maxY)
			return false;

		return true;
	}

	protected boolean isGridCacheOldRectangle(SpatialHashGrid<?> grid, float wcx, float wcy, float width, float height) {
		final var newMinX = grid.getCellIndexX((int) (wcx - width / 2));
		final var newMinY = grid.getCellIndexY((int) (wcy - height / 2));

		final var newMaxX = grid.getCellIndexX((int) (wcx + width / 2));
		final var newMaxY = grid.getCellIndexY((int) (wcy + height / 2));

		if (newMinX == minX && newMinY == minY && newMaxX == maxX && newMaxY == maxY)
			return false;

		return true;
	}

	protected boolean isGridCacheOldRectangle(SpatialHashGrid<?> grid, Rectangle aabb) {
		final var newMinX = grid.getCellIndexX((int) aabb.left());
		final var newMinY = grid.getCellIndexY((int) aabb.top());

		final var newMaxX = grid.getCellIndexX((int) aabb.right());
		final var newMaxY = grid.getCellIndexY((int) aabb.bottom());

		if (newMinX == minX && newMinY == minY && newMaxX == maxX && newMaxY == maxY)
			return false;

		return true;
	}

	public abstract void fillEntityBounds(SpatialHashGrid<?> grid);

	public abstract boolean isGridCacheOld(SpatialHashGrid<?> grid);

}
