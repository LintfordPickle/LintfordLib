package net.lintford.library.core.entity.instances;

import java.util.ArrayList;
import java.util.List;

/**
 * The {@link PoolInstanceManager} maintains an non-indexed array of pool items retreived and later returned for  re-use. 
 * Items given out by this pool are NOT tracked.
 */
public abstract class PoolInstanceManager<T extends PooledBaseData> extends InstanceManager<T> {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -2764687870288928563L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private transient List<T> mPooledItems;
	private int mEnlargePoolStepAmount = 8;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public int enlargePoolStepAmount() {
		return mEnlargePoolStepAmount;
	}

	public void enlargePoolStepAmount(int enlargeByAmount) {
		if (enlargeByAmount < 0)
			return;

		mEnlargePoolStepAmount = enlargeByAmount;
	}

	public T getInstanceByIndex(final int itemIndex) {
		if (itemIndex < 0 || itemIndex > instances().size() - 1)
			return null;

		return instances().get(itemIndex);
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public PoolInstanceManager() {
		this(0);
	}

	public PoolInstanceManager(int initialCapacity) {
		mPooledItems = new ArrayList<>();

		for (int i = 0; i < initialCapacity; i++) {
			mPooledItems.add(createPoolObjectInstance());
		}
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public T getFreePooledItem() {
		if (mPooledItems == null) {
			mPooledItems = new ArrayList<>();
		}

		final T lInst = mPooledItems.size() > 0 ? mPooledItems.remove(0) : enlargenInstancePool(mEnlargePoolStepAmount);
		mInstances.add(lInst);

		return lInst;
	}

	public void returnPooledItem(T returnItem) {
		if (returnItem == null)
			return;

		if (mInstances.contains(returnItem)) {
			mInstances.remove(returnItem);
		}

		if (mPooledItems == null) {
			mPooledItems = new ArrayList<>();
		}

		if (!mPooledItems.contains(returnItem)) {
			returnItem.reset();
			mPooledItems.add(returnItem);
		}
	}

	private T enlargenInstancePool(int enlargeByAmount) {
		for (int i = 0; i < enlargeByAmount; i++) {
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
