package net.lintford.library.core.physics.dynamics;

import net.lintford.library.ConstantsPhysics;
import net.lintford.library.core.geometry.partitioning.GridEntity;
import net.lintford.library.core.geometry.partitioning.SpatialHashGrid;

public abstract class RigidBodyEntity extends GridEntity {

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

		final var lUnitToPixels = ConstantsPhysics.UnitsToPixels();

		minX = grid.getCellIndexX((int) (aabb.left() * lUnitToPixels));
		minY = grid.getCellIndexY((int) (aabb.top() * lUnitToPixels));

		maxX = grid.getCellIndexX((int) (aabb.right() * lUnitToPixels));
		maxY = grid.getCellIndexY((int) (aabb.bottom() * lUnitToPixels));
	}

	@Override
	public boolean isGridCacheOld(SpatialHashGrid<?> grid) {
		final var aabb = body.aabb();

		final float newMinX = grid.getCellIndexX((int) (aabb.left() * ConstantsPhysics.UnitsToPixels()));
		final float newMinY = grid.getCellIndexY((int) (aabb.top() * ConstantsPhysics.UnitsToPixels()));

		final float newMaxX = grid.getCellIndexX((int) (aabb.right() * ConstantsPhysics.UnitsToPixels()));
		final float newMaxY = grid.getCellIndexY((int) (aabb.bottom() * ConstantsPhysics.UnitsToPixels()));

		if (newMinX == minX && newMinY == minY && newMaxX == maxX && newMaxY == maxY)
			return false; // early out

		return true;
	}
}
