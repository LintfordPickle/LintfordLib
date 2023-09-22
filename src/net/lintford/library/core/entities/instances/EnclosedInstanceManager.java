package net.lintford.library.core.entities.instances;

/**
 * An instance manager whose instances are maintained in an internal list. 
 * 
 * Whether or not an item has been assigned or is free is handled internally.
 */
public abstract class EnclosedInstanceManager<T extends ClosedPooledBaseData> extends InstanceManager<T> {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final int DEFAULT_ENLARGEN_POOL_AMOUNT = 8;
	public static final int MAXIMUM_ENLARGEN_POOL_AMOUNT = 256;

	// --------------------------------------
	// Constrcutor
	// --------------------------------------

	public EnclosedInstanceManager() {

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public T getFreeInstanceItem() {
		final int lNumInstances = mInstances.size();
		for (int i = 0; i < lNumInstances; i++) {
			if (!mInstances.get(i).isAssigned()) {
				mInstances.get(i).initInstance();
				return mInstances.get(i);
			}
		}

		final var lReturnInstance = enlargenInstancePool(DEFAULT_ENLARGEN_POOL_AMOUNT);
		lReturnInstance.initInstance();
		return lReturnInstance;
	}

	public void returnInstanceItem(T returnItem) {
		returnItem.setFree();
	}

	private T enlargenInstancePool(int enlargeByAmount) {
		if (enlargeByAmount <= 0 || enlargeByAmount > MAXIMUM_ENLARGEN_POOL_AMOUNT)
			enlargeByAmount = MAXIMUM_ENLARGEN_POOL_AMOUNT;

		for (int i = 0; i < enlargeByAmount; i++) {
			mInstances.add(createPoolObjectInstance());
		}

		final T lNewInstance = createPoolObjectInstance();
		mInstances.add(lNewInstance);

		return lNewInstance;
	}

	protected abstract T createPoolObjectInstance();

	public void resetAll() {
		final int lNumInstances = mInstances.size();
		for (int i = 0; i < lNumInstances; i++) {
			mInstances.get(i).setFree();
		}
	}
}
