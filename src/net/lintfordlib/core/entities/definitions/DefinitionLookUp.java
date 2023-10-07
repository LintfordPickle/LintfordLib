package net.lintfordlib.core.entities.definitions;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.TreeBidiMap;

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
	private BidiMap<Short, String> mDefinitionUidTable;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean containsDefinitionName(String definitionName) {
		return mDefinitionUidTable.containsValue(definitionName);
	}

	public boolean containsDefinitionUid(short definitionUid) {
		return mDefinitionUidTable.containsKey(definitionUid);
	}

	public short getDefinitionUidByName(String definitionName) {
		if (mDefinitionUidTable.containsValue(definitionName) == false)
			return DefinitionManager.NO_DEFINITION;
		return mDefinitionUidTable.getKey(definitionName);
	}

	public String getDefinitionNameByUid(short definitionUid) {
		if (mDefinitionUidTable.containsKey(definitionUid) == false)
			return null;
		return mDefinitionUidTable.get(definitionUid);
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
		mDefinitionUidTable = new TreeBidiMap<>();
	}

	public void addNewDefinition(short definitionUid, String definitionName) {
		mDefinitionUidTable.put(definitionUid, definitionName);
	}
}
