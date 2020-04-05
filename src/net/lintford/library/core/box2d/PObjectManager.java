package net.lintford.library.core.box2d;

import org.jbox2d.dynamics.World;

import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.box2d.definition.PObjectDefinition;
import net.lintford.library.core.box2d.entity.JBox2dEntityInstance;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.entity.definitions.DefinitionManager;
import net.lintford.library.core.entity.instances.PooledInstanceManager;

public class PObjectManager extends PooledInstanceManager<JBox2dEntityInstance> {

	private class PObjectRepository extends DefinitionManager<PObjectDefinition> {

		// --------------------------------------
		// Constructor
		// --------------------------------------

		public PObjectRepository() {
			loadDefinitions();

		}

		// --------------------------------------
		// Core-Methods
		// --------------------------------------

		@Override
		public void loadDefinitionsFromMetaFile(String pMetaFilepath) {

		}

		public void loadDefinitions() {
			loadDefinitionFromFile("res/pobjects/box.json");
			loadDefinitionFromFile("res/pobjects/circle.json");
			loadDefinitionFromFile("res/pobjects/test.json");

		}

		@Override
		public void loadDefinitionFromFile(String pFilepath) {
			final var lPObjectDefinition = new PObjectDefinition();
			lPObjectDefinition.loadFromFile(pFilepath, new StringBuilder(), null);

			addDefintion(lPObjectDefinition);

		}

	}

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -8661579477274556146L;

	public class PObjectMetaData {
		public String[] pobjectLocations;

	}

	// --------------------------------------
	// Variables
	// --------------------------------------

	private PObjectRepository mPObjectRepository;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public PObjectManager() {
		mPObjectRepository = new PObjectRepository();

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadGLContent(ResourceManager pResourceManager) {

	}

	public void unloadGLContent() {
//		final int lPObjectCount = mPObjectInstances.size();
//
//		for (int i = 0; i < lPObjectCount; i++) {
//			JBox2dEntityInstance lEntity = mPObjectInstances.get(i);
//
//			lEntity.unloadPhysics();
//
//		}

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public JBox2dEntityInstance getNewInstanceFromPObject(World pWorld, String pPObjectDefinitionName) {
		PObjectDefinition lPObjectDefinition = mPObjectRepository.getDefinitionByName(pPObjectDefinitionName);
		if (lPObjectDefinition == null) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Couldn't find PObject definition named " + pPObjectDefinitionName);
			return null;

		}

		return getNewInstanceFromPObject(pWorld, lPObjectDefinition);

	}

	public JBox2dEntityInstance getNewInstanceFromPObject(World pWorld, PObjectDefinition pPObjectDefinition) {
		final var lJBox2dEntityInstance = getFreePooledItem();

		if (lJBox2dEntityInstance == null)
			return null;

		// if nothing, then load physics from a definition ..
		lJBox2dEntityInstance.loadPObjectFromDefinition(pPObjectDefinition);

		// N.B. Even though we have an instance which mirrors the structure defined
		// in the PObject, we still need to call loadPhysics on it before it will be
		// added into the Box2d world!

		return lJBox2dEntityInstance;

	}

	@Override
	protected void returnPooledItem(JBox2dEntityInstance pReturnedItem) {
		if (pReturnedItem == null)
			return;

		pReturnedItem.unloadPhysics();

		super.returnPooledItem(pReturnedItem);
	}

	@Override
	protected JBox2dEntityInstance createPoolObjectInstance() {
		return new JBox2dEntityInstance(getNewInstanceUID());

	}

}
