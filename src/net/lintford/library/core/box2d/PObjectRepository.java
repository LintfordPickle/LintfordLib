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
		public boolean reloadPrevious;
	}

	public class PObjectMetaData {
		public PObjectMetaDataDefinition[] PObjectMetaDefinitions;

	}

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String POBJECT_BOX_NAME = "POBJECT_BOX";
	public static final String POBJECT_CIRCLE_NAME = "POBJECT_CIRCLE";

	public static final String MAIN_BODY_NAME = "MainBody";
	public static final String MAIN_FIXTURE_NAME = "MainFixture";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private short mDefinitionUidCounter;
	private Map<String, PObjectDefinition> mDefinitions;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public short getDefinitionUidCounter() {
		return mDefinitionUidCounter++;
	}

	public PObjectDefinition getPObjectDefinitionByName(String pName) {
		return mDefinitions.get(pName);

	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public PObjectRepository() {
		mDefinitions = new HashMap<>();

		mDefinitionUidCounter = 0;

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
			final var lReloadExisting = lPObjectDataDefinition.reloadPrevious;

			loadDefinitionFromFile(lPObjectName, lFilepath, lReloadExisting);

		}

	}

	private void loadSystemDefinitions() {
		// These two PObjects are added from embedded resources to ensure there is always an easy way
		// to create basic box2d shapes

		loadDefinitionFromFile(POBJECT_BOX_NAME, "/res/pobjects/pobjectBox.json", true);
		loadDefinitionFromFile(POBJECT_CIRCLE_NAME, "/res/pobjects/pobjectCircle.json", true);

	}

	public void loadDefinitionFromFile(String pPObjectName, String pFilepath, boolean pReloadExisting) {
		final var lExistingDefinition = mDefinitions.get(pPObjectName);
		if (!pReloadExisting && lExistingDefinition != null) {
			Debug.debugManager().logger().w(getClass().getSimpleName(), "PObject already exists (NO RELOAD). Skipping load");
			return;

		}

		final var lPObjectDefinition = new PObjectDefinition();
		lPObjectDefinition.loadPObjectDefinitionFromFile(pFilepath, new StringBuilder(), null);

		Debug.debugManager().logger().i(getClass().getSimpleName(), "PObject definition from '" + pFilepath + "' loaded as [" + pPObjectName + "]");
		lPObjectDefinition.definitionUid = getDefinitionUidCounter();

		if (lPObjectDefinition.isLoaded()) {
			mDefinitions.put(pPObjectName, lPObjectDefinition);

		}

	}

}
