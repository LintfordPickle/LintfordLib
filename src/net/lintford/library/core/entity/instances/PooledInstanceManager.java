package net.lintford.library.core.entity.instances;

import java.util.ArrayList;
import java.util.List;

import net.lintford.library.core.entity.PooledBaseData;

// TODO: Add a short description about the intended use of this class
public abstract class PooledInstanceManager<T extends PooledBaseData> extends InstanceManager<T> {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 355313071907418810L;

	private static final int DEFAULT_ENLARGEN_POOL_AMOUNT = 8;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private List<T> mPooledItems;

	// --------------------------------------
	// Constrcutor
	// --------------------------------------

	public PooledInstanceManager() {
		mPooledItems = new ArrayList<>();

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

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
