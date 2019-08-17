package net.lintford.library.core.entity.definitions;

import java.util.ArrayList;
import java.util.List;

import net.lintford.library.core.entity.BaseData;

public class DefinitionIDLUT extends BaseData {

	public class DefinitionNameID extends BaseData {

		// --------------------------------------
		// Constants
		// --------------------------------------

		private static final long serialVersionUID = 2264032487412665220L;

		// --------------------------------------
		// Variables
		// --------------------------------------

		public String name;
		public int defid;

		// --------------------------------------
		// Constructor
		// --------------------------------------

		public DefinitionNameID(String pName, int pDefID) {
			name = pName;
			defid = pDefID;

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

	public DefinitionIDLUT(List<BaseDefinition> pDefinitions) {
		mMapper = new ArrayList<>();

		final int lDefinitionCount = pDefinitions.size();
		for (int i = 0; i < lDefinitionCount; i++) {
			mMapper.add(new DefinitionNameID(pDefinitions.get(i).mDefinitionName, pDefinitions.get(i).mDefinitionID));

		}

	}

}
