package net.lintford.library.core.entity.instances;

import java.util.ArrayList;
import java.util.List;

/**
 * The {@link RetainedPoolInstanceManager} maintains both a pool of objects for re-use as well as a list of active instances. When an instance is drawn from the pool, it is automatically added to the instance list. When
 * the object is returned, it is removed from the instance list and re-added to the pool.
 * 
 */
public abstract class RetainedPoolInstanceManager<T extends RetainedPooledBaseData> extends InstanceManager<T> {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 355313071907418810L;

	private static final int DEFAULT_ENLARGEN_POOL_AMOUNT = 8;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private transient final List<T> mPooledItems;

	// --------------------------------------
	// Constrcutor
	// --------------------------------------

	public RetainedPoolInstanceManager() {
		mPooledItems = new ArrayList<>();

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void clearInstances() {
		while (mInstances.size() > 0) {
			final var lItem = mInstances.remove(0);
			if (!mPooledItems.contains(lItem)) {
				mPooledItems.add(lItem);

			}

		}

	}

	public T getFreePooledItem() {
		T lInst = null;

		if (mPooledItems.size() > 0)
			lInst = mPooledItems.remove(0);
		else
			lInst = enlargenInstancePool(DEFAULT_ENLARGEN_POOL_AMOUNT);

		return lInst;

	}

	public void returnPooledItem(T pReturnedItem) {
		if (!mPooledItems.contains(pReturnedItem)) {
			mPooledItems.add(pReturnedItem);
		}

	}

	private T enlargenInstancePool(int pAmt) {
		if (pAmt <= 0 || pAmt > 1000)
			pAmt = DEFAULT_ENLARGEN_POOL_AMOUNT;

		for (int i = 0; i < pAmt; i++) {
			mPooledItems.add(createPoolObjectInstance());

		}

		T lInst = mPooledItems.remove(0);

		return lInst;

	}

	protected abstract T createPoolObjectInstance();

}
