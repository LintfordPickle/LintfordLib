package net.lintford.library.core.entity.instances;

import java.util.ArrayList;
import java.util.List;

import net.lintford.library.core.entity.BaseData;

public class InstanceManager<T extends BaseData> extends BaseData {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 7491642462693673597L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected List<T> mInstances;
	private int mInstanceUIDCounter;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public List<T> instances() {
		return mInstances;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public InstanceManager() {
		mInstances = new ArrayList<>();

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	// --------------------------------------
	// Methods
	// --------------------------------------

	protected int getNewInstanceUID() {
		return mInstanceUIDCounter++;
	}

}
