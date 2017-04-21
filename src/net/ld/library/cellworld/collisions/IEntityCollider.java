package net.ld.library.cellworld.collisions;

import net.ld.library.cellworld.CircleEntity;
import net.ld.library.cellworld.RectangleEntity;

public interface IEntityCollider {

	public abstract void checkEntityCollisions(CircleEntity pEntity);
	
	public abstract void checkEntityCollisions(RectangleEntity pEntity);
	
}
