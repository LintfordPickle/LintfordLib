package net.lintford.library.core.collisions;

import net.lintford.library.core.entity.WorldEntity;

public interface IEntityCollider {

	public abstract void checkEntityCollisions(WorldEntity pEntity);
	
}
