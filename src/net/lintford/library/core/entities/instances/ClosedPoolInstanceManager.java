package net.lintford.library.core.entities.instances;

import java.util.ArrayList;
import java.util.List;

/**
 * The {@link} pre-allocated, indexed instances and maintains them within a pool until they are required. The instances are then
 * maintains within a separate list until they are returned to the pool. All instances can be tracked with the main.
 */
public abstract class ClosedPoolInstanceManager<T extends ClosedPooledBaseData> extends InstanceManager<T> {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final int DEFAULT_ENLARGEN_POOL_AMOUNT = 8;
	public static final int MAXIMUM_ENLARGEN_POOL_AMOUNT = 256;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private List<T> mPooledItems;
	private int mEnlargePoolStepAmount;

	private int mEntityInstanceCounter;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public int getNewInstanceUID() {
		return mEntityInstanceCounter++;
	}

	public int enlargePoolStepAmount() {
		return mEnlargePoolStepAmount;
	}

	public void enlargePoolStepAmount(int enlargeByAmount) {
		if (enlargeByAmount < 0)
			return;

		mEnlargePoolStepAmount = enlargeByAmount;
	}

	public T getInstanceByUid(int uid) {
		final int lNumInstances = mInstances.size();
		for (int i = 0; i < lNumInstances; i++) {
			if (mInstances.get(i).uid == uid)
				return mInstances.get(i);
		}
		return null;
	}

	public T getInstanceByIndex(final int itemIndex) {
		if (itemIndex < 0 || itemIndex > instances().size() - 1)
			return null;

		return instances().get(itemIndex);
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ClosedPoolInstanceManager() {
		this(0);
	}

	public ClosedPoolInstanceManager(int initialCapacity) {
		mEntityInstanceCounter = 0;
		mEnlargePoolStepAmount = DEFAULT_ENLARGEN_POOL_AMOUNT;
		mPooledItems = new ArrayList<>();

		for (int i = 0; i < initialCapacity; i++) {
			mPooledItems.add(createPoolObjectInstance());
		}
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	/** Sets the mEntityInstanceCounter to the next available instance counter */
	protected void refreshInstanceUidCounter() {
		final int lNumInstances = mInstances.size();
		for (int i = 0; i < lNumInstances; i++) {
			if (mInstances.get(i).uid > mEntityInstanceCounter)
				mEntityInstanceCounter = mInstances.get(i).uid;
		}

		final int lNumPooledInstances = mPooledItems.size();
		for (int i = 0; i < lNumPooledInstances; i++) {
			if (mPooledItems.get(i).uid > mEntityInstanceCounter)
				mEntityInstanceCounter = mPooledItems.get(i).uid;
		}
		
		mEntityInstanceCounter++;
	}

	public T getFreePooledItem() {
		if (mPooledItems == null) {
			mPooledItems = new ArrayList<>();
		}

		final T lInst = mPooledItems.size() > 0 ? mPooledItems.remove(0) : enlargenInstancePool(mEnlargePoolStepAmount);

		if (mInstances.contains(lInst) == false)
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
		if (enlargeByAmount > MAXIMUM_ENLARGEN_POOL_AMOUNT)
			enlargeByAmount = MAXIMUM_ENLARGEN_POOL_AMOUNT;

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
