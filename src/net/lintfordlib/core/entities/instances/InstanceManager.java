package net.lintfordlib.core.entities.instances;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * The {@link InstanceManager} maintains an array of instances, beyond which, instances are not tracked.
 */
public abstract class InstanceManager<T extends Object> {

	// --------------------------------------
	// Variables
	// --------------------------------------

	@SerializedName(value = "instances")
	protected final List<T> mInstances = new ArrayList<>();

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

	public InstanceManager() {

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void clearInstances() {
		mInstances.clear();
	}
}