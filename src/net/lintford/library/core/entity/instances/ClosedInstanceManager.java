package net.lintford.library.core.entity.instances;

/**
 * An instance manager whose instances are maintained in an internal list. Whether or not an item has been assigned or is free is handled internally.
 * There is no pool of {@link ClosedInstanceBaseData} items with this instance manager.
 * This class differs to {@link PreAllocatedInstanceManager} in that the internal list of {@link ClosedInstanceBaseData} will grow to meet demand.
 */
public abstract class ClosedInstanceManager<T extends ClosedInstanceBaseData> extends InstanceManager<T> {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 355313071907418810L;

	private static final int DEFAULT_ENLARGEN_POOL_AMOUNT = 8;

	// --------------------------------------
	// Constrcutor
	// --------------------------------------

	public ClosedInstanceManager() {

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
		if (enlargeByAmount <= 0 || enlargeByAmount > 1000)
			enlargeByAmount = DEFAULT_ENLARGEN_POOL_AMOUNT;

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
