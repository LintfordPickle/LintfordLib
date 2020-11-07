package net.lintford.library.core.entity.instances;

import java.util.ArrayList;
import java.util.List;

import net.lintford.library.core.entity.BaseInstanceData;

public abstract class InstanceManager<T extends BaseInstanceData> extends BaseInstanceData {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 7491642462693673597L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected final List<T> mInstances = new ArrayList<>();
	private int mInstanceUIDCounter;

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

	public InstanceManager() {

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void clearInstances() {
		mInstances.clear();
	}

	protected int getNewInstanceUID() {
		return mInstanceUIDCounter++;
	}

}
