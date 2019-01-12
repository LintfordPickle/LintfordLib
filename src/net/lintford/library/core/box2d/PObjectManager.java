package net.lintford.library.core.box2d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbox2d.dynamics.World;

import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.box2d.definition.PObjectDefinition;
import net.lintford.library.core.box2d.entity.JBox2dEntityInstance;
import net.lintford.library.core.debug.Debug;

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

	protected Map<String, List<JBox2dEntityInstance>> mPObjectPoolMap;
	private List<JBox2dEntityInstance> mJBox2dEntityInstancePool;
	private List<JBox2dEntityInstance> mPObjectInstances;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public PObjectManager() {
		mPObjectDefinitions = new HashMap<>();
		mPObjectPoolMap = new HashMap<>();

		mJBox2dEntityInstancePool = new ArrayList<>();
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
			JBox2dEntityInstance lEntity = mPObjectInstances.get(i);

			lEntity.unloadPhysics();

		}

	}

	public void loadPObjectsFromMetaFile(String pFilename) {

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	// Definitions

	public PObjectDefinition loadPObjectDefinition(String pObjectName, String pFilename) {
		if (mPObjectDefinitions.containsKey(pObjectName)) {
			return mPObjectDefinitions.get(pObjectName);

		}

		PObjectDefinition lPObjectDefinition = new PObjectDefinition();
		lPObjectDefinition.loadFromFile(pFilename, new StringBuilder(), null);
		lPObjectDefinition.name(pObjectName);

		mPObjectDefinitions.put(pObjectName, lPObjectDefinition);

		return lPObjectDefinition;

	}

	public PObjectDefinition getPObjectDefinition(String pObjectName) {
		if (mPObjectDefinitions.containsKey(pObjectName)) {
			return mPObjectDefinitions.get(pObjectName);

		}

		return null;
	}

	// Instances

	public JBox2dEntityInstance getNewInstanceFromPObject(World pWorld, String pPObjectDefinitionName) {
		PObjectDefinition lPObjectDefinition = mPObjectDefinitions.get(pPObjectDefinitionName);
		if (lPObjectDefinition == null) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Couldn't find PObject definition named " + pPObjectDefinitionName);
			return null;

		}

		return getNewInstanceFromPObject(pWorld, lPObjectDefinition);

	}

	public JBox2dEntityInstance getNewInstanceFromPObject(World pWorld, PObjectDefinition pPObjectDefinition) {
		JBox2dEntityInstance lNewEntity = null;//getFreeBox2dBodyInstance();

		// First look for a freed instance in the pool?
		List<JBox2dEntityInstance> lPoolList = mPObjectPoolMap.get(pPObjectDefinition.name());

		if (lPoolList != null && lPoolList.size() > 0) {
			lNewEntity = lPoolList.remove(0);

		}

		// If there was no instance to recycle, then get a new instance from the 'generic' pool.
		if (lNewEntity == null)
			lNewEntity = getFreeBox2dBodyInstance();

		// if nothing, then load physics from a definition ..
		lNewEntity.loadPObjectFromDefinition(pPObjectDefinition);

		// Still need to call loadPhysics on this object before it will be instantiated into the world

		return lNewEntity;

	}

	private JBox2dEntityInstance getFreeBox2dBodyInstance() {
		final int lPoolSize = mJBox2dEntityInstancePool.size();
		for (int i = 0; i < lPoolSize; i++) {
			JBox2dEntityInstance lRetInst = mJBox2dEntityInstancePool.get(i);
			if (lRetInst.isFree()) {
				mJBox2dEntityInstancePool.remove(lRetInst);
				return lRetInst;

			}
		}

		return increasePoolSize(10);

	}

	private JBox2dEntityInstance increasePoolSize(int pAmt) {
		JBox2dEntityInstance lReturnInst = new JBox2dEntityInstance();
		mJBox2dEntityInstancePool.add(lReturnInst);

		for (int i = 0; i < pAmt; i++) {
			mJBox2dEntityInstancePool.add(new JBox2dEntityInstance());
		}

		return lReturnInst;

	}

}
