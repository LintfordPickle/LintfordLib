package net.lintford.library.core.box2d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbox2d.dynamics.World;

import net.lintford.library.core.box2d.definition.PObjectDefinition;
import net.lintford.library.core.box2d.entity.JBox2dEntity;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.graphics.ResourceManager;

public class PObjectManager {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public class PObjectMetaData {
		public String[] pobjectLocations;

	}

	// --------------------------------------
	// Variables
	// --------------------------------------

	private Map<String, PObjectDefinition> mPObjectDefinitions; // these are loaded from PObject files

	private List<JBox2dEntity> mPObjectInstances;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public PObjectManager() {
		mPObjectDefinitions = new HashMap<>();
		mPObjectInstances = new ArrayList<>();

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadGLContent(ResourceManager pResourceManager) {

	}

	public void unloadGLContent() {
		final int lPObjectCount = mPObjectInstances.size();

		for (int i = 0; i < lPObjectCount; i++) {
			JBox2dEntity lEntity = mPObjectInstances.get(i);

			lEntity.unloadPhysics();

		}

	}

	public void loadPObjectDefinitionsFromMeta(final String pMetaFileLocation) {

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public PObjectDefinition loadPObjectDefinition(String pObjectName, String pFilename) {
		if (mPObjectDefinitions.containsKey(pObjectName)) {
			return mPObjectDefinitions.get(pObjectName);

		}

		PObjectDefinition lPObjectDefinition = new PObjectDefinition();
		lPObjectDefinition.loadFromFile(pFilename, new StringBuilder(), null);

		mPObjectDefinitions.put(pObjectName, lPObjectDefinition);

		return lPObjectDefinition;

	}

	public PObjectDefinition getPObjectDefinition(String pObjectName) {
		if (mPObjectDefinitions.containsKey(pObjectName)) {
			return mPObjectDefinitions.get(pObjectName);

		}

		return null;
	}

	public JBox2dEntity getNewInstanceFromPObject(World pWorld, String pPObjectDefinitionName) {
		PObjectDefinition lPObjectDefinition = mPObjectDefinitions.get(pPObjectDefinitionName);
		if (lPObjectDefinition == null) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Couldn't find PObject definition named " + pPObjectDefinitionName);
			return null;

		}

		return getNewInstanceFromPObject(pWorld, lPObjectDefinition);

	}

	public JBox2dEntity getNewInstanceFromPObject(World pWorld, PObjectDefinition pPObjectDefinition) {
		JBox2dEntity lNewEntity = new JBox2dEntity(); // Create a new instance so we can save the state
		lNewEntity.loadPhysicsFromDefinition(pPObjectDefinition, pWorld);

		return lNewEntity;

	}

}
