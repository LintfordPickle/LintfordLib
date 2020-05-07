
package net.lintford.library.core.box2d;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.GsonBuilder;

import net.lintford.library.core.box2d.definition.PObjectDefinition;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.storage.FileUtils;

public class PObjectRepository {

	public class PObjectMetaDataDefinition {
		public String filepath;
		public String pObjectName;
		public boolean reloadable;
	}

	public class PObjectMetaData {
		public PObjectMetaDataDefinition[] PObjectMetaDefinitions;

	}

	// --------------------------------------
	// Constants
	// --------------------------------------

	// Default pobjects which are always available
	public static final String POBJECT_DEFINITION_BOX = "POBJECT_BOX";
	public static final String POBJECT_DEFINITION_CIRCLE = "POBJECT_CIRCLE";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private Map<String, PObjectDefinition> mDefinitions;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public PObjectDefinition getPObjectDefinitionByName(String pName) {
		return mDefinitions.get(pName);

	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public PObjectRepository() {
		mDefinitions = new HashMap<>();

		loadSystemDefinitions();

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadDefinitionsFromMetaFile(String pMetaFileLocation) {
		Debug.debugManager().logger().i(getClass().getSimpleName(), String.format("Loading PObjects from meta-file %s", pMetaFileLocation));

		final var lGson = new GsonBuilder().create();

		String lMetaFileContentsString = null;
		PObjectMetaData lPObjectMetaData = null;

		lMetaFileContentsString = FileUtils.loadString(pMetaFileLocation);
		lPObjectMetaData = lGson.fromJson(lMetaFileContentsString, PObjectMetaData.class);

		if (lPObjectMetaData == null || lPObjectMetaData.PObjectMetaDefinitions == null || lPObjectMetaData.PObjectMetaDefinitions.length == 0) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "There was an error reading the PObject meta file");
			return;

		}

		final int lNumberOfPObjectDefinitions = lPObjectMetaData.PObjectMetaDefinitions.length;
		for (int i = 0; i < lNumberOfPObjectDefinitions; i++) {
			final var lPObjectDataDefinition = lPObjectMetaData.PObjectMetaDefinitions[i];

			final var lPObjectName = lPObjectDataDefinition.pObjectName;
			final var lFilepath = lPObjectDataDefinition.filepath;
			final var lReloadable = lPObjectDataDefinition.reloadable;

			loadDefinitionFromFile(lPObjectName, lFilepath, lReloadable);

		}
	}

	private void loadSystemDefinitions() {
		// These two PObjects are added from embedded resources to ensure there is always an easy way
		// to create basic box2d shapes

		loadDefinitionFromFile(POBJECT_DEFINITION_BOX, "res/pobjects/box.json", true);
		loadDefinitionFromFile(POBJECT_DEFINITION_CIRCLE, "res/pobjects/circle.json", true);

	}

	public void loadDefinitionFromFile(String pPObjectName, String pFilepath, boolean pReload) {
		final var lExistingDefinition = mDefinitions.get(pPObjectName);
		if (!pReload && lExistingDefinition != null) {
			Debug.debugManager().logger().w(getClass().getSimpleName(), "PObject already exists (NO RELOAD). Skipping load");
			return;

		}

		final var lPObjectDefinition = new PObjectDefinition();
		lPObjectDefinition.loadFromFile(pFilepath, new StringBuilder(), null);

		Debug.debugManager().logger().i(getClass().getSimpleName(), "PObject definition from '" + pFilepath + "' loaded as [" + pPObjectName + "]");

		mDefinitions.put(pPObjectName, lPObjectDefinition);

	}

}
