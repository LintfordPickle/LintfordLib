package net.lintfordlib.core.entities.instances;

/**
 * The {@link OpenPoolInstanceManager} creates Entities which are first created within a pool, but are released from the pool upon assignment.
 * The {@link OpenPooledBaseData} instances can be returned for reuse. 
 * 
 * The pool can be enlargened at any time (pre-allocation).
 * */
public abstract class OpenPoolInstanceManager<T extends OpenPooledBaseData> extends InstanceManager<T> {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final int DEFAULT_ENLARGEN_POOL_AMOUNT = 8;
	public static final int MAXIMUM_ENLARGEN_POOL_AMOUNT = 256;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private int mEnlargePoolStepAmount;

	private int mInstanceUidCounter;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public int enlargePoolStepAmount() {
		return mEnlargePoolStepAmount;
	}

	public void enlargePoolStepAmount(int enlargeStepAmount) {
		if (enlargeStepAmount < 0)
			return;

		mEnlargePoolStepAmount = enlargeStepAmount;
	}

	public T getInstanceByIndex(final int itemIndex) {
		if (itemIndex < 0 || itemIndex > instances().size() - 1)
			return null;

		return instances().get(itemIndex);
	}

	public T getInstanceByUid(final int entityUid) {
		final int lNumInstances = numInstances();
		for (int i = 0; i < lNumInstances; i++) {
			if (instances().get(i).uid == entityUid)
				return instances().get(i);
		}

		return null;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public OpenPoolInstanceManager() {
		this(0);
	}

	public OpenPoolInstanceManager(int initialCapacity) {
		mEnlargePoolStepAmount = DEFAULT_ENLARGEN_POOL_AMOUNT;

		for (int i = 0; i < initialCapacity; i++) {
			mInstances.add(createPoolObjectInstance());
		}
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public T getFreePooledItem() {
		T lInst = null;

		if (mInstances.size() > 0) {
			return mInstances.remove(0);

		} else {
			lInst = enlargenInstancePool(mEnlargePoolStepAmount);
			mInstances.add(lInst);
		}

		return lInst;
	}

	public void returnPooledItem(T returnedItem) {
		if (returnedItem == null)
			return;

		if (mInstances.contains(returnedItem) == false) {
			mInstances.add(returnedItem);
		}
	}

	private T enlargenInstancePool(int enlargeByAmount) {
		if (enlargeByAmount > MAXIMUM_ENLARGEN_POOL_AMOUNT)
			enlargeByAmount = MAXIMUM_ENLARGEN_POOL_AMOUNT;

		for (int i = 0; i < enlargeByAmount; i++) {
			mInstances.add(createPoolObjectInstance());
		}

		T lInst = createPoolObjectInstance();

		return lInst;
	}

	protected abstract T createPoolObjectInstance();

	@Override
	public void clearInstances() {
	}

	protected int getNewInstanceUID() {
		return mInstanceUidCounter++;
	}

	public int getInstanceCounter() {
		return mInstanceUidCounter;
	}

	public void setInstanceCounter(int instanceCounter) {
		mInstanceUidCounter = instanceCounter;
	}

}
