package net.lintfordlib.core.physics.dynamics;

import net.lintfordlib.ConstantsPhysics;
import net.lintfordlib.core.geometry.partitioning.GridEntity;
import net.lintfordlib.core.geometry.partitioning.SpatialHashGrid;
import net.lintfordlib.core.maths.Transform;

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

	public Transform transform() {
		if (body == null)
			return Transform.Identity;

		return body.transform;
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

	public void unloadPhysicsBody() {
		body = null;
	}

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
