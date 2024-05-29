package net.lintfordlib.core.entities.instances;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * The {@link PoolInstanceManager} maintains an array of instances, beyond which, instances are not tracked.
 */
public abstract class PoolInstanceManager<T extends Object> {

	// --------------------------------------
	// Variables
	// --------------------------------------

	@SerializedName(value = "instances")
	protected final List<T> mInstances = new ArrayList<>();
	protected transient final List<T> mInstancePool = new ArrayList<>();

	// --------------------------------------
	// Properties
	// --------------------------------------

	public int numInstances() {
		return mInstances.size();
	}

	public List<T> instances() {
		return mInstances;
	}

	public T getInstanceByIndex(int index) {
		if (index < 0 || index >= mInstances.size())
			return null;

		return mInstances.get(index);
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public PoolInstanceManager() {

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void clearInstances() {
		final var lNumInstances = mInstances.size();
		for (int i = 0; i < lNumInstances; i++) {
			if (mInstancePool.contains(mInstances.get(i)) == false)
				mInstancePool.add(mInstances.get(i));
		}

		mInstances.clear();
	}

	public void returnInstance(T instance) {
		if (mInstances.contains(instance))
			mInstances.remove(instance);

		if (mInstancePool.contains(instance) == false)
			mInstancePool.add(instance);
	}

	public T getFreePooledItem() {
		if (mInstancePool.size() > 0) {
			final var lItem = mInstancePool.remove(0);
			return lItem;
		}

		return createNewInstance();
	}

	protected abstract T createNewInstance();
}