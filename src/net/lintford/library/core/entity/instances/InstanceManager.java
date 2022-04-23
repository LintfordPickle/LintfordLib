package net.lintford.library.core.entity.instances;

import java.util.ArrayList;
import java.util.List;

import net.lintford.library.core.entity.BaseInstanceData;

/**
 * The {@link InstanceManager} maintains an array of serializable instances which extend {@link BaseInstanceData}. Instances are not otherwise tracked.
 */
public abstract class InstanceManager<T extends BaseInstanceData> extends BaseInstanceData {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 7491642462693673597L;

	// --------------------------------------
	// Variables
	// --------------------------------------

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

	public T getInstanceByIndex(int pIndex) {
		if (pIndex < 0 || pIndex >= mInstances.size())
			return null;
		return mInstances.get(pIndex);
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