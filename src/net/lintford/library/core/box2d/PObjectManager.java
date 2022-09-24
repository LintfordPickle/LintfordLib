package net.lintford.library.core.box2d;

import org.jbox2d.dynamics.World;

import net.lintford.library.ConstantsPhysics;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.box2d.definition.PObjectDefinition;
import net.lintford.library.core.box2d.entities.JBox2dEntityInstance;
import net.lintford.library.core.box2d.instance.Box2dInstanceManager;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.entity.instances.IndexedPoolInstanceManager;

public class PObjectManager extends IndexedPoolInstanceManager<JBox2dEntityInstance> {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -8661579477274556146L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private Box2dInstanceManager mBox2dInstanceManager;
	private PObjectDefinitionRepository mPObjectRepository;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public Box2dInstanceManager box2dInstanceManager() {
		return mBox2dInstanceManager;
	}

	public PObjectDefinitionRepository definitionRepository() {
		return mPObjectRepository;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public PObjectManager() {
		mPObjectRepository = new PObjectDefinitionRepository();
		mBox2dInstanceManager = new Box2dInstanceManager();
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadResources(ResourceManager pResourceManager) {

	}

	public void unloadResources() {

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public JBox2dEntityInstance getNewInstanceBoxInstance(World pWorld, int pBodyType, float pWorldWidth, float pWorldHeight) {
		final var lPObjectDefinition = mPObjectRepository.getPObjectDefinitionByName(PObjectDefinitionRepository.POBJECT_BOX_NAME);
		//		if (lPObjectDefinition == null) {
		//			Debug.debugManager().logger().e(getClass().getSimpleName(), "Couldn't find PObjectDefinition named " + PObjectRepository.POBJECT_BOX_NAME);
		//			return null;
		//
		//		}

		final var lJBox2dInstance = getNewInstanceFromPObject(pWorld, lPObjectDefinition);
		lJBox2dInstance.setFixtureDimensions(PObjectDefinitionRepository.MAIN_FIXTURE_NAME, ConstantsPhysics.toUnits(pWorldWidth), ConstantsPhysics.toUnits(pWorldHeight));
		lJBox2dInstance.setBodyType(PObjectDefinitionRepository.MAIN_BODY_NAME, pBodyType);

		return lJBox2dInstance;

	}

	public JBox2dEntityInstance getNewInstanceCircleInstance(World pWorld, int pBodyType, float pWorldRadius) {
		final var lPObjectDefinition = mPObjectRepository.getPObjectDefinitionByName(PObjectDefinitionRepository.POBJECT_CIRCLE_NAME);
		if (lPObjectDefinition == null) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Couldn't find PObjectDefinition named " + PObjectDefinitionRepository.POBJECT_CIRCLE_NAME);
			return null;

		}

		final var lJBox2dInstance = getNewInstanceFromPObject(pWorld, lPObjectDefinition);
		lJBox2dInstance.setFixtureRadius(PObjectDefinitionRepository.MAIN_FIXTURE_NAME, ConstantsPhysics.toUnits(pWorldRadius));
		lJBox2dInstance.setBodyType(PObjectDefinitionRepository.MAIN_BODY_NAME, pBodyType);

		return lJBox2dInstance;

	}

	public JBox2dEntityInstance getNewInstanceFromPObject(World pWorld, String pPObjectDefinitionName) {
		final var lPObjectDefinition = mPObjectRepository.getPObjectDefinitionByName(pPObjectDefinitionName);
		if (lPObjectDefinition == null) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Couldn't find PObjectDefinition named " + pPObjectDefinitionName);
			return null;

		}

		return getNewInstanceFromPObject(pWorld, lPObjectDefinition);

	}

	public JBox2dEntityInstance getNewInstanceFromPObject(World pWorld, PObjectDefinition pPObjectDefinition) {
		final var lJBox2dEntityInstance = getFreePooledItem();

		if (lJBox2dEntityInstance == null)
			return null;

		// if nothing, then load physics from a definition ..
		lJBox2dEntityInstance.loadPObjectFromDefinition(mBox2dInstanceManager, pPObjectDefinition);

		// N.B. Even though we have an instance which mirrors the structure defined
		// in the PObject, we still need to call loadPhysics on it before it will be
		// added into the Box2d world!

		return lJBox2dEntityInstance;

	}

	@Override
	public void returnPooledItem(JBox2dEntityInstance pReturnedItem) {
		if (pReturnedItem == null)
			return;

		pReturnedItem.returnPooledInstances(mBox2dInstanceManager);

		super.returnPooledItem(pReturnedItem);
	}

	@Override
	protected JBox2dEntityInstance createPoolObjectInstance() {
		return new JBox2dEntityInstance(getNewInstanceUID());

	}

}
