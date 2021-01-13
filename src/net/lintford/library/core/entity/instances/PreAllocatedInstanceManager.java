package net.lintford.library.core.entity.instances;

import java.util.ArrayList;
import java.util.List;

import net.lintford.library.core.entity.BaseInstanceData;

/** All the instances in a pre-allocated instance manager are, as tthe name suggests, pre-allocated during creation and and then tracked by an internal
 * isAssigned flag. */
public abstract class PreAllocatedInstanceManager<T extends PreAllocatedInstanceData> extends BaseInstanceData {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 7491642462693673597L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected final List<T> mInstances = new ArrayList<>();
	private int mInstanceUIDCounter;
	private int mCapacity;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public int numInstances() {
		return mInstances.size();
	}

	public List<T> instances() {
		return mInstances;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public PreAllocatedInstanceManager(int pCapacity) {
		mCapacity = pCapacity;

		preAllocateInstances();

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	private void preAllocateInstances() {
		for (int i = 0; i < mCapacity; i++) {
			final var lNewInstance = createNewInstance();
			lNewInstance.internalIsAssigned(false);

			mInstances.add(lNewInstance);

		}

	}

	public void resetInstances() {
		for (int i = 0; i < mCapacity; i++) {
			final var lNewInstance = mInstances.get(i);
			lNewInstance.reset();
			lNewInstance.internalIsAssigned(false);

		}

	}

	protected int getNewInstanceUID() {
		return mInstanceUIDCounter++;

	}

	protected abstract T createNewInstance();

}
