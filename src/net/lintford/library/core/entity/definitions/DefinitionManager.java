package net.lintford.library.core.entity.definitions;

import java.util.ArrayList;
import java.util.List;

import net.lintford.library.core.entity.BaseData;

public abstract class DefinitionManager<T extends BaseDefinition> extends BaseData {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -1729184288330735542L;

	private DefinitionIDLUT createNewDefinitionLUT() {
		return null;

	}

	private void convertInstances() {

	}

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected DefinitionIDLUT mCurrentDefinitionIDLUT;
	protected List<T> mDefinitions;

	private transient int mDefinitionUIDCounter;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public List<T> definitions() {
		return mDefinitions;
	}

	public int getNewDefinitionUID() {
		return mDefinitionUIDCounter++;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public DefinitionManager() {
		mDefinitions = new ArrayList<>();
		mDefinitionUIDCounter = 0;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	protected abstract void loadDefinitions(String pMetaFilepath);

	@SuppressWarnings("unchecked")
	public void initialize(String pMetafilepath) {
		loadDefinitions(pMetafilepath);

		// First check to see if the mCurrentDefinitionIDLUT is loaded (has it been deserialized?)
		// because if not, then this is probably the first time we have run the level
		if (mCurrentDefinitionIDLUT == null) {
			createNewDefinitionLUT();

		} else {
			// otherwise compare the two LUTs, and if they differ, then we need to convert to the old definition IDs to the new versions
			// 1. Create a temp LUT
			DefinitionIDLUT lTempDefinitionLUT = new DefinitionIDLUT((List<BaseDefinition>) mDefinitions);

			// Compare the previously saved LUT with the new LUT and convert if necessary
			if (lTempDefinitionLUT.version() != mCurrentDefinitionIDLUT.version()) {
				convertInstances();

			} else {
				// no conversion is necessary
				mCurrentDefinitionIDLUT = lTempDefinitionLUT;

			}

		}

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public T getDefinitionByName(String pName) {
		final int lDefinitionCount = mDefinitions.size();
		for (int i = 0; i < lDefinitionCount; i++) {
			if (mDefinitions.get(i).mDefinitionName.equals(pName)) {
				return mDefinitions.get(i);
			}

		}

		return null;

	}

	public T getDefinitionByID(int pDefID) {
		final int lDefinitionCount = mDefinitions.size();
		for (int i = 0; i < lDefinitionCount; i++) {
			if (mDefinitions.get(i).mDefinitionID == pDefID) {
				return mDefinitions.get(i);
			}

		}

		return null;

	}

	public T getDefinitionByIndex(int pDefIndex) {
		if (pDefIndex >= 0 && pDefIndex < mDefinitions.size())
			return mDefinitions.get(pDefIndex);

		return null;

	}

}