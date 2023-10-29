package net.lintfordlib.core.physics.spatial;

import net.lintfordlib.core.entities.Entity;

public abstract class PhysicsGridEntity extends Entity {

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	// -caching
	public int minUnitX;
	public int minUnitY;

	public int maxUnitX;
	public int maxUnitY;

	public int queryId;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public boolean isOnGrid() {
		return minUnitX != -1 && minUnitY != -1 && maxUnitX != -1 && maxUnitY != -1;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public PhysicsGridEntity(int entityUid) {
		super(entityUid);
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public void clearGridCache() {
		queryId = 0;
		minUnitX = 0;
		maxUnitX = 0;
		minUnitY = 0;
		maxUnitY = 0;
	}

	public abstract void fillEntityBounds(PhysicsHashGrid<?> grid);

	public abstract boolean isGridCacheOld(PhysicsHashGrid<?> grid);

}
