package net.lintford.library.core.collisions;

import net.lintford.library.data.entities.WorldEntity;

public interface IEntityCollider {

	public abstract void checkEntityCollisions(WorldEntity pEntity);
	
}
