package net.lintfordlib.core.entities.definitions;

import java.util.HashMap;
import java.util.Map;

import net.lintfordlib.core.entities.savedefinitions.BaseSaveDefinition;

public class DefinitionLookUp extends BaseSaveDefinition {

	public class DefinitionNameUid extends BaseSaveDefinition {

		// --------------------------------------
		// Constants
		// --------------------------------------

		private static final long serialVersionUID = 2264032487412665220L;

		// --------------------------------------
		// Variables
		// --------------------------------------

		public final String mDefinitionName;
		public final int mDefinitionUid;

		// --------------------------------------
		// Properties
		// --------------------------------------

		public final String definitionName() {
			return mDefinitionName;
		}

		public final int definitionUid() {
			return mDefinitionUid;
		}

		// --------------------------------------
		// Constructor
		// --------------------------------------

		public DefinitionNameUid(String definitionName, int definitionUid) {
			mDefinitionName = definitionName;
			mDefinitionUid = definitionUid;
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
	private Map<Short, String> mUidToName;
	private Map<String, Short> mNameToUid;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean containsDefinitionName(String definitionName) {
		return mNameToUid.containsKey(definitionName);
	}

	public boolean containsDefinitionUid(short definitionUid) {
		return mUidToName.containsKey(definitionUid);
	}

	public short getDefinitionUidByName(String definitionName) {
		Short uid = mNameToUid.get(definitionName);
		return uid != null ? uid : DefinitionManager.NO_DEFINITION;
	}

	public String getDefinitionNameByUid(short definitionUid) {
		return mUidToName.get(definitionUid);
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

	public DefinitionLookUp() {
		mUidToName = new HashMap<>();
		mNameToUid = new HashMap<>();
	}

	public void addNewDefinition(short definitionUid, String definitionName) {
		mUidToName.put(definitionUid, definitionName);
		mNameToUid.put(definitionName, definitionUid);
	}
}