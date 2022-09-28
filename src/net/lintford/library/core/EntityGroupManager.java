package net.lintford.library.core;

public abstract class EntityGroupManager {

	public abstract int increaseReferenceCounts(int entityGroupUid);

	public abstract int decreaseReferenceCounts(int entityGroupUid);

}
