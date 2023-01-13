package net.lintford.library.core.physics.spatial;

import net.lintford.library.core.entity.Entity;

public abstract class PhysicsGridEntity extends Entity {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	private static final long serialVersionUID = -8790552505437118157L;

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	public int collEntityType;

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

	public PhysicsGridEntity(int entityUid) {
		super(entityUid);
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

	public abstract void fillEntityBounds(PhysicsHashGrid<?> grid);

	public abstract boolean isGridCacheOld(PhysicsHashGrid<?> grid);

}
