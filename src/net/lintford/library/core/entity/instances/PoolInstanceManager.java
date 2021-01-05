package net.lintford.library.core.entity.instances;

import java.util.ArrayList;
import java.util.List;

/**
 * The {@link PoolInstanceManager} maintains an arraylist as well as an instance list of items which can be returned to the pool for later re-use.
 * Items given out by this pool are NOT tracked. (See {@ RetainedPoolInstanceManager for the alternative).
 */
public abstract class PoolInstanceManager<T extends PooledBaseData> extends InstanceManager<T> {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -2764687870288928563L;

	private static final int DEFAULT_ENLARGEN_POOL_AMOUNT = 8;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private transient List<T> mPooledItems;

	public T getInstanceByIndex(final int pItemIndex) {
		if (pItemIndex < 0 || pItemIndex > instances().size() - 1)
			return null;

		return instances().get(pItemIndex);

	}

	public T getInstanceByUid(final int pPoolUid) {
		final int lNumInstances = numInstances();
		for (int i = 0; i < lNumInstances; i++) {
			if (instances().get(i).poolUid == pPoolUid)
				return instances().get(i);
		}

		return null;

	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public PoolInstanceManager() {
		mPooledItems = new ArrayList<>();

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public T getFreePooledItem() {
		T lInst = null;

		if (mPooledItems == null) {
			mPooledItems = new ArrayList<>();

		}

		if (mPooledItems.size() > 0) {
			lInst = mPooledItems.remove(0);
			mInstances.add(lInst);

		} else {
			lInst = enlargenInstancePool(DEFAULT_ENLARGEN_POOL_AMOUNT);
			mInstances.add(lInst);

		}

		return lInst;

	}

	public void returnPooledItem(T pReturnedItem) {
		if (mInstances.contains(pReturnedItem)) {
			mInstances.remove(pReturnedItem);

		}

		if (mPooledItems == null) {
			mPooledItems = new ArrayList<>();

		}

		if (!mPooledItems.contains(pReturnedItem)) {
			pReturnedItem.reset();
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

	@Override
	public void clearInstances() {
		mPooledItems.addAll(mInstances);
		mInstances.clear();

	}
}
