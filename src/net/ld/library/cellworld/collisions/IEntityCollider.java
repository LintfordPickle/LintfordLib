package net.ld.library.cellworld.collisions;

import net.ld.library.cellworld.entities.CellEntity;

public interface IEntityCollider<T extends CellEntity> {
	
	public abstract void checkEntityCollisions(T pEntity);
	
}