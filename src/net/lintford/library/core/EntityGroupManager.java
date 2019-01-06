package net.lintford.library.core;

public abstract class EntityGroupManager {

	public abstract int increaseReferenceCounts(int pEntityGroupID);

	public abstract int decreaseReferenceCounts(int pEntityGroupID);

}
