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

	protected Map<String, PObjectDefinition> mPObjectDefinitions; // these are loaded from PObject files
	protected List<JBox2dEntityInstance> mJBox2dEntityInstancePool;
	protected List<JBox2dEntityInstance> mPObjectInstances;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public PObjectManager() {
		mPObjectDefinitions = new HashMap<>();
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

	// --------------------------------------
	// Methods
	// --------------------------------------

	// Definitions

	public PObjectDefinition loadPObjectDefinition(String pFilename) {
		final var lPObjectDefinition = new PObjectDefinition();
		lPObjectDefinition.loadFromFile(pFilename, new StringBuilder(), null);

		mPObjectDefinitions.put(lPObjectDefinition.name(), lPObjectDefinition);

		return lPObjectDefinition;

	}

	public PObjectDefinition getPObjectDefinition(String pObjectName) {
		if (mPObjectDefinitions.containsKey(pObjectName)) {
			return mPObjectDefinitions.get(pObjectName);

		}

		return null;
	}

	public JBox2dEntityInstance getNewInstanceFromPObject(World pWorld, String pPObjectDefinitionName) {
		PObjectDefinition lPObjectDefinition = mPObjectDefinitions.get(pPObjectDefinitionName);
		if (lPObjectDefinition == null) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Couldn't find PObject definition named " + pPObjectDefinitionName);
			return null;

		}

		return getNewInstanceFromPObject(pWorld, lPObjectDefinition);

	}

	public JBox2dEntityInstance getNewInstanceFromPObject(World pWorld, PObjectDefinition pPObjectDefinition) {
		JBox2dEntityInstance lNewEntity = getFreeBox2dEntityInstance();

		if (lNewEntity == null)
			return null;

		// if nothing, then load physics from a definition ..
		lNewEntity.loadPObjectFromDefinition(pPObjectDefinition);

		// N.B. Even though we have an instance which mirrors the structure defined
		// in the PObject, we still need to call loadPhysics on it before it will be
		// added into the Box2d world!

		return lNewEntity;

	}

	private JBox2dEntityInstance getFreeBox2dEntityInstance() {
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

	public void returnBox2dEntityInstance(JBox2dEntityInstance pObject) {
		if (pObject == null)
			return;

		pObject.unloadPhysics();

		if (mJBox2dEntityInstancePool.contains(pObject))
			mJBox2dEntityInstancePool.add(pObject);

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
