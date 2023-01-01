package net.lintford.library.core.collisions;

import net.lintford.library.core.geometry.partitioning.GridEntity;
import net.lintford.library.core.geometry.partitioning.SpatialHashGrid;

public abstract class RigidBodyEntity extends GridEntity {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 8301457813068268466L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected transient RigidBody body;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean isInitialized() {
		return body != null;
	}

	// protect reference
	public RigidBody body() {
		return body;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public RigidBodyEntity(int entityUid, int collEntityType) {
		super(entityUid, collEntityType);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void fillEntityBounds(SpatialHashGrid<?> grid) {
		final var aabb = body.aabb();

		minX = grid.getCellIndexX((int) aabb.left());
		minY = grid.getCellIndexY((int) aabb.top());

		maxX = grid.getCellIndexX((int) aabb.right());
		maxY = grid.getCellIndexY((int) aabb.bottom());
	}

	@Override
	public boolean isGridCacheOld(SpatialHashGrid<?> grid) {
		final var aabb = body.aabb();

		final float newMinX = grid.getCellIndexX((int) aabb.left());
		final float newMinY = grid.getCellIndexY((int) aabb.top());

		final float newMaxX = grid.getCellIndexX((int) aabb.right());
		final float newMaxY = grid.getCellIndexY((int) aabb.bottom());

		if (newMinX == minX && newMinY == minY && newMaxX == maxX && newMaxY == maxY)
			return false; // early out

		return true;
	}
}
