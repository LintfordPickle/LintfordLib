package net.lintford.library.core.entity.definitions;

import java.util.ArrayList;
import java.util.List;

import net.lintford.library.core.entity.BaseData;

public class DefinitionIDLUT<T extends BaseDefinition> extends BaseData {

	// This class is needed because the BaseDefinition is not serializable.
	public class DefinitionNameID extends BaseData {

		// --------------------------------------
		// Constants
		// --------------------------------------

		private static final long serialVersionUID = 2264032487412665220L;

		// --------------------------------------
		// Variables
		// --------------------------------------

		public String definitionName;
		public int definitionID;

		// --------------------------------------
		// Constructor
		// --------------------------------------

		public DefinitionNameID(String pName, int pDefID) {
			definitionName = pName;
			definitionID = pDefID;

		}

	}

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 5528451061486008262L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private String mVersion;
	private List<DefinitionNameID> mMapper;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public List<DefinitionNameID> mapper() {
		return mMapper;
	}

	public String version() {
		if (mVersion == null)
			mVersion = calculateVersion();

		return mVersion;
	}

	private String calculateVersion() {
		return "";
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public DefinitionIDLUT(List<T> pDefinitions) {
		mMapper = new ArrayList<>();

		final int lDefinitionCount = pDefinitions.size();
		for (int i = 0; i < lDefinitionCount; i++) {
			BaseDefinition lBaseDef = pDefinitions.get(i);
			mMapper.add(new DefinitionNameID(lBaseDef.name, lBaseDef.definitionID));

		}

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public boolean compareContents(List<DefinitionNameID> pOtherIDLUT) {
		final int lMapperSize = mMapper.size();
		final int lOtherMapperSize = pOtherIDLUT.size();

		if (lMapperSize == 0)
			return true; // because there is nothing to actually compare

		for (int i = 0; i < lMapperSize; i++) {
			DefinitionNameID lOriginalDefinition = mMapper.get(i);

			boolean lFound = false;

			// Now, we need to find the same definitionName in the other list and compare the IDs.
			// If either the name is not found, or the ID is different, then we have a mismatch and can early exit
			for (int j = 0; j < lOtherMapperSize; j++) {
				DefinitionNameID lOtherDefinition = pOtherIDLUT.get(i);

				if (lOriginalDefinition.definitionName.equals(lOtherDefinition.definitionName)) {
					if (lOriginalDefinition.definitionID != lOtherDefinition.definitionID)
						return false;

				}
				
				lFound = true;

			}

			if (!lFound)
				return false;

		}

		// If we got this far, then all matches
		return true;

	}

}
